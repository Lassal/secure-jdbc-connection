package br.lassal.security;


import br.lassal.security.rsa.PrivKeyDecriptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class AppConfig {



    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPlainPassword;

    @Value("${spring.datasource.crypto-password}")
    private String dbCryptoPassword;

    @Value("classpath:${spring.datasource.security.keystore}")
    private Resource keystore;

    @Value("${spring.datasource.security.keystore.password-env}")
    private String keystorePasswordEnv;

    @Value("${spring.datasource.security.keystore.keyalias}")
    private String keystoreKeyAlias;

    @Value("${companyname.app.name}")
    private String appName;

    @Value("${acme.app.process}")
    private String processName;

    private String dbPassword;

    /***
     * Retrieves the informed process name retrieve from program parameters
     * returns a string with max 12 characters size (Oracle limitation)
     * @return
     */
    private String getProcessName(){
        String processName;
         if(this.processName != null && this.processName.length() > 0){
             processName =  this.processName;
         }
         else{
             processName = "DEFAULT";
         }

         // return max size V$SESSION.Process
         return processName.substring(0,Math.min(processName.length(),12));
    }

    private String getDbPassword() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, IOException {
        if(this.dbPassword == null){
            this.dbPassword = this.getDecriptedPassword();

            if(this.dbPassword == null){
                this.dbPassword = this.dbPlainPassword;
            }
        }

        return this.dbPassword;
    }

    private String getDecriptedPassword() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        String password = null;

        if(this.keystore != null && this.keystoreKeyAlias != null){
            PrivKeyDecriptor decriptor = new PrivKeyDecriptor(this.keystore.getFile(), keystoreKeyAlias);
            password = decriptor.decript(this.dbCryptoPassword);
        }

        return password;
    }

    @Bean
    public DataSource dataSource() throws SQLException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {

        return this.getHikariDataSource();

    }

    private DataSource getHikariDataSource() throws SQLException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, IOException {
        HikariConfig hc = new HikariConfig("/connection-pool.properties");
        hc.setDataSource(this.getOracleDataSource());

        return new HikariDataSource(hc);
    }

    private OracleDataSource getOracleDataSource()
              throws SQLException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(this.dbUrl);
        ods.setUser(this.dbUser);
        ods.setPassword(this.getDbPassword());


        Properties conProp = new Properties();
        conProp.put("v$session.program", this.appName);
        conProp.put("v$session.process",  this.getProcessName());

        ods.setConnectionProperties(conProp);


        return ods;
    }

}

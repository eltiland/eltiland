package com.eltiland.resource;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import sun.misc.IOUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

            ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:spring-context.xml");

            WorkerBean worker = (WorkerBean) ctx.getBean("workerBean");
            worker.doJob();

    }


}

package vsb;

import vsb.database.DBConnector;
import vsb.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vsb.restAPI.service.SqlCommandService;
import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

import javax.xml.bind.JAXBException;

@SpringBootApplication
//@ComponentScan(basePackages = {"vsb", "vsb.restAPI"})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }
}
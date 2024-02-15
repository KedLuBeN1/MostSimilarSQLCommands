package vsb.xml;

import vsb.xml.model.SqlStatements;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XmlReader {

    public static SqlStatements readXml(File xmlFile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(SqlStatements.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (SqlStatements) unmarshaller.unmarshal(xmlFile);
    }
}

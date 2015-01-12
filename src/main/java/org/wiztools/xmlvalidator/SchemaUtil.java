package org.wiztools.xmlvalidator;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author subWiz
 */
public class SchemaUtil {
    private SchemaUtil(){}


    public static Schema getWXSSchema(File file, final boolean warnings, final boolean verbose) throws SAXException {
        SchemaFactory fac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        fac.setErrorHandler(new ErrorHandler() {        	
        	public void fatalError(SAXParseException e) throws SAXException {
                throw e;
            }
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }
            public void warning(SAXParseException e) throws SAXException {
            	if (warnings)
            		throw e;
            	else if (verbose)
            		System.out.println(e.toString());
            }
        });
        return fac.newSchema(file);
    }

    public static Schema getRNGSchema(File file) throws SAXException {
        SchemaFactory fac = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        return fac.newSchema(file);
    }

    public static void validate(Schema schema, File file) throws IOException, SAXException {
        Validator validator = schema.newValidator();
        Source source = new StreamSource(file);
        validator.validate(source);
    }
}

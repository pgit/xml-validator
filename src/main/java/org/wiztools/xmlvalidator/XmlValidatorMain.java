package org.wiztools.xmlvalidator;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;

/**
 *
 * @author subWiz
 */
public class XmlValidatorMain {
    @Argument(value="s", alias="wxs", description = "W3C Schema (XSD)")
    private String wxsSchema;

    @Argument(value="r", alias="rng", description = "RELAX NG Schema")
    private String rngSchema;

    @Argument(value="h", alias="help", description = "Displays help")
    boolean isHelp;

    @Argument(value="v", alias="verbose", description = "Verbose output")
    boolean isVerbose;
    
    @Argument(value="w", alias="warnings", description = "Treat warnings as errors")
    boolean isWarnings;

    private static final String lineSeparator = System.getProperty("line.separator");
    private static final String doubleLineSeparator = lineSeparator + lineSeparator;

    // All the messages that need to be displayed:
    private static final String msg_compile_success = "Schema Compile Successful: [%1$s]" + lineSeparator;
    private static final String msg_compile_fail = "Error compiling schema: %1$s:" + doubleLineSeparator;
    private static final String msg_validation_success = "Validation Successful: [%1$s] against [%2$s]" + lineSeparator;
    private static final String msg_validation_fail = "Error validating [%1$s] against [%2$s]:" + doubleLineSeparator;

    public static void main(String[] arg) throws IOException {
        XmlValidatorMain parser = new XmlValidatorMain();
        List<String> extras = Args.parse(parser, arg);

        if(parser.isHelp) {
            Args.usage(parser);
            System.exit(0);
        }

        if(parser.rngSchema == null && parser.wxsSchema == null) {
            System.err.println("Needs one of -s or -r parameter.");
            Args.usage(parser);
            System.exit(1);
        }

        // If there are failures during execution, exit with non-0 code
        boolean hasFailures = false;

        // Compile the schemas:
        Schema wxs = null;
        try{
            wxs = parser.wxsSchema!=null? SchemaUtil.getWXSSchema(new File(parser.wxsSchema), parser.isVerbose, parser.isWarnings): null;
            if(wxs != null && parser.isVerbose) {
                System.out.printf(msg_compile_success, parser.wxsSchema);
            }
        }
        catch(SAXException ex) {
            hasFailures = true;
            System.err.printf(msg_compile_fail, parser.wxsSchema);
            System.err.println(ex.getMessage());
        }
        Schema rng = null;
        try{
            rng = parser.rngSchema!=null? SchemaUtil.getRNGSchema(new File(parser.rngSchema)): null;
            if(rng != null && parser.isVerbose) {
                System.out.printf(msg_compile_success, parser.rngSchema);
            }
        }
        catch(SAXException ex) {
            hasFailures = true;
            System.err.printf(msg_compile_fail, parser.rngSchema);
            System.err.println(ex.getMessage());
        }

        // Validate the XML files against the schemas:
        for(String f: extras) {
            try{
                if(wxs != null) SchemaUtil.validate(wxs, new File(f));
                if(wxs != null && parser.isVerbose) {
                    System.out.printf(msg_validation_success, f, parser.wxsSchema);
                }
            }
            catch(SAXException ex) {
                hasFailures = true;
                System.err.printf(msg_validation_fail, f, parser.wxsSchema);
                System.err.println(ex.getMessage());
            }
            try{
                if(rng != null) SchemaUtil.validate(rng, new File(f));
                if(rng != null && parser.isVerbose) {
                    System.out.printf(msg_validation_success, f, parser.rngSchema);
                }
            }
            catch(SAXException ex) {
                hasFailures = true;
                System.err.printf(msg_validation_fail, f, parser.rngSchema);
                System.err.println(ex.getMessage());
            }
        }

        if(hasFailures) {
            System.exit(2);
        }
     }
}

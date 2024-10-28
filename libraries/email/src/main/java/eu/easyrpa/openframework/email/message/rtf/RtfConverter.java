package eu.easyrpa.openframework.email.message.rtf;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfListenerAdaptor;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.rtf.CommandType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

public class RtfConverter extends RtfListenerAdaptor {
    private Command currentDestination = Command.rtf;
    private final Deque<Command> destinationStack = new ArrayDeque<>();
    private final StringBuilder buffer = new StringBuilder();

    public String convertToText(String rtfContent) throws IOException {
        return convertToText(rtfContent.getBytes(StandardCharsets.UTF_8));
    }

    public String convertToText(byte[] rtfBytes) throws IOException {
        buffer.setLength(0);
        InputStream bodyIS = new ByteArrayInputStream(rtfBytes);
        IRtfSource source = new RtfStreamSource(bodyIS);
        IRtfParser parser = new StandardRtfParser();
        currentDestination = Command.rtf;
        parser.parse(source, this);
        return buffer.toString();
    }

    /**
     * This method is called to process the test we've extracted from the RTF file.
     */
    public void processExtractedText(String text) {
        buffer.append(text);
    }

    @Override
    public void processGroupStart() {
        destinationStack.push(currentDestination);
    }

    @Override
    public void processGroupEnd() {
        currentDestination = destinationStack.pop();
    }

    @Override
    public void processString(String string) {
//        System.out.println("S: " + string);
        switch (currentDestination) {
            case rtf:
            case htmlrtf:
//            case pntext:
            case htmltag:
//            case fldrslt:
                processExtractedText(string);
                break;

            default:
                break;
        }
    }

    @Override
    public void processCommand(Command command, int parameter, boolean hasParameter, boolean optional) {
//        System.out.println(command + "   " + command.getCommandType() + "  " + parameter + "  " + hasParameter + "  " + optional);
        if (command.getCommandType() == CommandType.Destination) {
            currentDestination = command;
        }
        switch (command) {
            case par:
            case line:
//            case row:
                processExtractedText("\n");
                break;
            case tab:
//            case cell:
                processExtractedText("\t");
                break;
            default:
                break;
        }
    }
}

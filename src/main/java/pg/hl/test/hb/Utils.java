package pg.hl.test.hb;

import org.hibernate.boot.model.naming.Identifier;

public class Utils {
    public static String quoteName(String string){
        Identifier name = Identifier.toIdentifier(string);
        if (name == null) {
            return null;
        } else {
            return (name.isQuoted() ? name : Identifier.quote(name)).toString();
        }
    }
}

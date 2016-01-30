/**A JSON object is one of the following:
 * -Number (integer, floating point, etc)
 * -String (anything enclosed by quotes)
 * -Boolean (true/false), no quotes)
 * -Array (anything in square brackets "[]", comma separated)
 * -Object (anything in curly braces "{}", comma separated)
 * -null (the word "null" without quotes)
 */
public class Value {
    private Class type;
    private Object value;

    public Value(Object o) {
        //System.out.println("Creating value for object " + o);
        if(o == null) { //null
            //System.out.println("Value is null");
            value = null; type = null; return;
        } else if(o instanceof Boolean) { //true, false
            //System.out.println("Value is boolean");
            value = o; type = Boolean.class; return;
        } else if(o instanceof String) {
            String s = (String)o;
            if((s).startsWith("\"")) { //String
                //System.out.println("Value is a String");
                value = (s).substring(1, (s).length() - 1); type = String.class; return; 
            } else if ((s).startsWith("{")) { //JSON object

                //System.out.println("Value is a JSON object");
                value = JsonObject.jsonify(s); type = JsonObject.class; return;
            } else if ((s).startsWith("[")) { //Array
                //System.out.println("Value is a JSON array");
                value = JsonArray.jsonify(s); type = JsonArray.class; return;
            } else { //Double or maybe Boolean as a string
                try {
                    Double d = Double.parseDouble((s));
                    value = d;

                    //System.out.println("Value is a number");
                    type = Double.class; return;
                } catch (Exception e) {}

                if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
                    value = Boolean.parseBoolean(s);
                    type = Boolean.class; return;
                }
            }
        }

        //System.out.println("Value is unknown!");
    }

    public Object asNull() {
        return null;
    }

    public boolean asBoolean() {
        return (Boolean) value;
    }

    public String asString() {
        return (String) value;
    }

    public double asDouble() {
        return (Double) value;
    }

    public int asInteger() {
        return ((Double)value).intValue();
    }

    public JsonObject asJsonObject() {
        return (JsonObject) value;
    }

    public JsonArray asJsonArray() {
        return (JsonArray) value;
    }

    public Class getType() {
        return type;
    }

    public Object value() {
        return value;
    }

    public String toString() {
        if(value == null) return "null";
        //if(value instanceof String) return "\"" + value + "\"";
        if(value instanceof Double && ((Double)value).intValue() == (Double)value) return "" + ((Double)value).intValue();
        return value.toString();
    }
}
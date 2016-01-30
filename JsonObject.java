import java.util.*;

/**
 * A JSON object is one of the following:
 * -Number (integer, floating point, etc)
 * -String (anything enclosed by quotes)
 * -Boolean (true/false), no quotes)
 * -Array (anything in square brackets "[]", comma separated)
 * -Object (anything in curly braces "{}", comma separated)
 * -null (the word "null" without quotes)
 * 
 * A subobject is a String object, followed by a semicolon ":", with 
 * any valid JSON object following it.
 * 
 * A JSON object can contain any of none of the aforementioned elements,
 * each separated by commas.
 * 
 * EX:
 * {}
 * 
 * {
 *     "test1":"hello",
 *     "test2": {
 *         "subtest1":"meow",
 *         "array": ["a", "b", "c"]
 *     }
 * }
 */
public class JsonObject {
    private ArrayList<KeyValuePair> pairs = new ArrayList<>();

    public JsonObject() {
        pairs = new ArrayList<>();
    }

    public JsonObject(ArrayList<KeyValuePair> pairs) {
        this.pairs = pairs;
    }

    public static JsonObject jsonify(String source) {
        source = removeWhiteSpace(source);

        JsonObject obj = new JsonObject();

        int index = 0;
        while(index != -1) {
            //Read in the next string
            if((index = source.indexOf("\"", index)) == -1) break;
            String key = source.substring(index + 1, index = source.indexOf("\"", index + 1));
            if((index+=2) > source.length()) break; //Skip the semicolon

            //Read in the next value
            Value value = null;
            if(source.charAt(index) == '{') //Json object
                value = new Value(source.substring(index, index = endOfJsonObject(source, index) + 1));
            else if(source.charAt(index) == '[') //Array
                value = new Value(source.substring(index, index = endOfArray(source, index) + 1));
            else if(source.charAt(index) == '"') //String
                value = new Value(source.substring(index, index = endOfString(source, index) + 1));
            else if(source.charAt(index) == 't') { //'true'
                value = new Value(true); index+=4; }
            else if(source.charAt(index) == 'f') { //'false'
                value = new Value(false); index+=5; }
            else if(source.charAt(index) == 'n') { //'null'
                value = new Value(null); index+=4; }
            else {                               //number
                int i = source.indexOf(",", index + 1);
                if(i == -1) i = source.indexOf("}", index + 1);
                
                value = new Value(source.substring(index, index = i));
            }

            obj.pairs.add(new KeyValuePair(key, value));
        }
        return obj;
    }

    /**
     * Gets the value at the "index" numbered index in this JsonObject.
     * 
     * This method is meant to be used when the JsonObject is static every
     * time, like when calling from a server with a known response. This method
     * can then be used to somewhat simplify the code by treating this JsonObject
     * like an ArrayList.
     * 
     * @param index The index of the key-value pair to return.
     * @return The "index"-th value in this JsonObject.
     */
    public Value get(int index) {
        return pairs.get(index).value;
    }

    /**
     * Gets the value that matches the given key, if this JsonObject contains
     * the given key. Otherwise, returns null.
     * @return The Value associated with the given key.
     * @param key A String containing the name of a key to match in this object.
     */
    public Value get(String key) {
        for(KeyValuePair k : pairs)
            if(k.key.equals(key))
                return k.value;
        return null;
    }

    /**
     * Add a given key-value pair to this JsonObject.
     * 
     * Valid types for value: any number, JsonObject, JsonArray,
     * String, true, false, and null.
     * 
     * @param key A String containing the name of the key to add.
     * @param value An Object of one of the above types representing
     *  the value meant to be associated with the given key.
     */
    public void add(String key, Object value) {
        pairs.add(new KeyValuePair(key, new Value(value.toString())));
    }

    /**
     * Returns an ArrayList containing all of the String keys in this
     * JsonObject.
     */
    public ArrayList<String> getKeyList() {
        ArrayList<String> list = new ArrayList<>();
        for(KeyValuePair k : pairs)
            list.add(k.key);
        return list;
    }

    //Removes whitespace
    private static String removeWhiteSpace(String source) {
        StringBuilder builder = new StringBuilder();
        boolean inQuotes = false;

        for(int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            //If we see a quote, toggle the quote boolean, unless it's an escaped
            //quote, except when we're at the 0th index; then it's always a toggle.
            if(c == '"') 
                if((i > 0 && source.charAt(i - 1) != '\\') || i == 0) 
                    inQuotes = !inQuotes;

            //Add any character that isn't whitespace, unless it's inside quotes.
            if(!inQuotes) 
                if(c == ' ' || c == '\n' || c == '\t') 
                    continue;

            builder.append(c);
        }

        return builder.toString();
    }

    //Returns the index of the next matching quote that isn't escaped
    private static int endOfString(String source, int startIndex) {
        int index = startIndex;
        while(index++ < source.length()) {
            char c = source.charAt(index);
            if(c == '"') 
                if((index > 0 && source.charAt(index - 1) != '\\') || index == 0) 
                    return index;
        }
        return -1;
    }

    //Returns the index of the next matching curly brace of the same nesting level
    private static int endOfJsonObject(String source, int startIndex) {
        int nestingLevel = 1, index = startIndex;
        while(nestingLevel != 0) {
            char c = source.charAt(++index);
            if(c == '{') nestingLevel++;
            if(c == '}') nestingLevel--;
        }
        return index;
    }

    //Returns the index of the next matching right bracket of the same nesting level
    private static int endOfArray(String source, int startIndex) {
        int nestingLevel = 1, index = startIndex;
        while(nestingLevel != 0) {
            char c = source.charAt(++index);
            if(c == '[') nestingLevel++;
            if(c == ']') nestingLevel--;
        }
        return index;
    }    

    /**
     * Returns a String representation of this JsonObject.
     * 
     * This String is formatted in such a way that
     * JsonObject.jsonify(aJsonObject.toString()) equals aJsonObject.
     * 
     * @return A String representing this JsonObject.
     */
    public String toString() {
        String s = "{";
        for(KeyValuePair k : pairs) {
            s+=k.toString()+",";
        }
        return s.substring(0, s.length() - 1) + "}";
    }

    /**
     * Returns a String representation of this JsonObject, but also formats
     * the String in a more easy to read fashion. 
     * 
     * As a general rule, JsonObjects that are nested internally increase in
     * indentation, and every key/value pair takes up its own line. 
     */
    public String format() {
        return format(1);
    }

    private String format(int i) {
        String s = "{\n";
        for(KeyValuePair k : pairs) {
            s += indent(i) + "\"" + k.key + "\": ";
            if(k.value.getType() == String.class)
                s += "\"" + k.value + "\"";
            else if(k.value.getType() == JsonObject.class) {
                s += k.value.asJsonObject().format(i + 1);
            } else
                s += k.value.toString();
            s += ",\n";
        }
        return s.substring(0, s.length() - 2) + "\n" + indent(i - 1) + "}";
    }

    /**
     * This is a private helper method that simply gives back a String
     * containing 'indentNumber' tabs.
     * 
     * @param indentNumber The number of tabs in the return String.
     * @return an indentation String that consists of 'indentNumber' tabs.
     */
    private String indent(int indentNumber) {
        String indent = "";
        for(int i = 0; i < indentNumber; i++)
            indent += "\t";
        return indent;
    }

    private static class KeyValuePair {
        private String key;
        private Value value;

        public KeyValuePair(String key, Value value) {
            this.key = key;
            this.value = value;
        }

        //Make sure we wrap the keys and values in quotes so that a JsonObject's toString() can be turned back into the same JsonObject
        public String toString() {
            return "\"" + key + "\":" + (value.getType() == String.class ? "\"" + value + "\"" : value.toString());
        }
    }
}
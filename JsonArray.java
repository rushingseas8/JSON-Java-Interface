import java.util.ArrayList;

public class JsonArray {
    private ArrayList<Value> values;

    public JsonArray() {
        values = new ArrayList<>();
    }

    public JsonArray(ArrayList<Value> values) {
        this.values = values;
    }

    public static JsonArray jsonify(String source) {
        source = removeWhiteSpace(source);
        JsonArray toReturn = new JsonArray();

        int index = source.indexOf("[") + 1;
        while(index != 0) {
            //System.out.println("index = " +  index + " char at that index: " + source.charAt(index));

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
            else                                 //number
                value = new Value(source.substring(index, index = source.indexOf(",", index + 1)));

            toReturn.values.add(value);
            index = source.indexOf(",", index) + 1;
        }

        return toReturn;
    }

    public int size() {
        return values.size();
    }

    public Value get(int index) {
        return values.get(index);
    }

    private static String removeWhiteSpace(String source) {
        StringBuilder builder = new StringBuilder();
        boolean inQuotes = false;

        for(int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            if(c == '"') 
                if((i > 0 && source.charAt(i - 1) != '\\') || i == 0) 
                    inQuotes = !inQuotes;

            if(!inQuotes) 
                if(c == ' ' || c == '\n' || c == '\t') 
                    continue;

            builder.append(c);
        }

        return builder.toString();
    }

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

    /**
     * A private helper method that returns the index of the curly brace
     *  that matches the one at "startIndex".
     * @precondition: source.charAt(startIndex) == '{'
     * @param source The source string to search through.
     * @param startIndex The index to start searching at.
     * @return The index of the next right curly brace ('}') that matches
     *  the given left curly brace
     */
    private static int endOfJsonObject(String source, int startIndex) {
        int nestingLevel = 1, index = startIndex;
        while(nestingLevel != 0) {
            char c = source.charAt(++index);
            if(c == '{') nestingLevel++;
            if(c == '}') nestingLevel--;
        }
        return index;
    }

    /**
     * A private helper method that returns the index of the square bracket
     *  that matches the one at "startIndex".
     * @precondition: source.charAt(startIndex) == '['
     * @param source The source string to search through.
     * @param startIndex The index to start searching at.
     * @return The index of the next right bracket (']') that matches
     *  the given left bracket
     */
    private static int endOfArray(String source, int startIndex) {
        int nestingLevel = 1, index = startIndex;
        while(nestingLevel != 0) {
            char c = source.charAt(++index);
            if(c == '[') nestingLevel++;
            if(c == ']') nestingLevel--;
        }
        return index;
    }    

    public String toString() {
        String s = "";
        for(Value v : values) 
            s += (v.getType() == String.class ? "\"" + v + "\"" : v.toString()) + ",";
        return "[" + s.substring(0, s.length() - 1) + "]";
    }
}
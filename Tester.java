public class Tester {
    public static void main() {
        String j1 = "{\"test1\":\"hello\",\"test2\":{\"subtest1\":\"meow\",\"array\": [\"a\", \"b\", \"c\"]}}";
        JsonObject obj = JsonObject.jsonify("{\"test1\":\"hello\",\"test2\":{\"subtest1\":\"meow\",\"array\": [\"a\", \"b\", \"c\"]}}"); 
        System.out.println("Raw: " + j1);
        System.out.println("New: " + obj);
        System.out.println(obj.format());
        
        JsonObject obj2 = JsonObject.jsonify("{\"glossary\": {\"title\": \"example glossary\",\"GlossDiv\": {\"title\": \"S\",\"GlossList\": {\"GlossEntry\": {\"ID\": \"SGML\",\"SortAs\": \"SGML\",\"GlossTerm\": \"Standard Generalized Markup Language\",\"Acronym\": \"SGML\",\"Abbrev\": \"ISO 8879:1986\",\"GlossDef\": {\"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\": [\"GML\", \"XML\"]},\"GlossSee\": \"markup\"}}}}}");
        System.out.println(obj2);
        System.out.println(obj2.format());

        JsonArray arr = JsonArray.jsonify("{\"testArray\": [1, 2.0, 3.1415926, \"testValue\", true, false, null, {\"test\":\"meow\"}, [1, 2, \"nesting\"]]}");
        System.out.println(arr);

        System.out.println(obj.get("test1"));
        System.out.println(obj.get("test2"));
        System.out.println(obj.get("nope"));
        obj.add("test3", 16.0);
        System.out.println(obj.get("test3"));
        
        System.out.println("Before cycling: " + obj);
        for(int i = 0; i < 100; i++) {
            obj = JsonObject.jsonify(obj.toString());
        }
        System.out.println("After cycling : " + obj);
    }
}
package yanndub.parser;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;

public class TestParser {

	@Test
	public void testAnyCar() {
		Optional<ParserResult<Character>> testChar = Parser.parse(Parser.anyCar(), "Test");
		assertEquals('T', (char) testChar.get().getResult());
		testChar = Parser.parse(Parser.anyCar(), "");
		assertEquals(Optional.empty(), testChar);
	}
	
	@Test
	public void testFail() {
		assertEquals(Optional.empty(), Parser.parse(Parser.fail(), ""));
	}
	
	@Test
	public void testSuccesful() {
		MoocObjectValue obj = new MoocObjectValue();
		Optional<ParserResult<MoocObjectValue>> result = Parser.parse(Parser.succesful(obj), "timoleon");
		assertEquals(Optional.of(new ParserResult<MoocObjectValue>(obj, "timoleon")), result);
	}
	
	@Test
	public void testBind() {
		Parser<Character> ps = Parser.bind(Parser.anyCar(), c -> Parser.succesful(c));
		Optional<ParserResult<Character>> testChar = Parser.parse(ps, "Test");
		assertEquals('T', (char) testChar.get().getResult());
		
		ps = Parser.bind(Parser.anyCar(), c -> Parser.fail());
		testChar = Parser.parse(ps, "Test");
		assertEquals(Optional.empty(), testChar);
	}
	
	@Test
	public void testAlternate() {
		MoocObjectValue obj = new MoocObjectValue();
		Parser<MoocObjectValue> ps = Parser.alternate(Parser.fail(), Parser.succesful(obj));
		Optional<ParserResult<MoocObjectValue>> testChar = Parser.parse(ps, "Timoleon");
		assertEquals(obj, testChar.get().getResult());
		
		ps = Parser.alternate(Parser.succesful(obj), Parser.fail());
		testChar = Parser.parse(ps, "Timoleon");
		assertEquals(obj, testChar.get().getResult());
	
		ps = Parser.alternate(Parser.fail(), Parser.fail());
		testChar = Parser.parse(ps, "Timoleon");
		assertEquals(Optional.empty(), testChar);
	}
	
	@Test
	public void testCharCond() {
		Optional<ParserResult<Character>> result = Parser.parse(Parser.charCond(c -> c == 'T'), "Test");
		assertEquals('T', (char) result.get().getResult());
		
		result = Parser.parse(Parser.charCond(c -> c == 'T'), "PF");
		assertEquals(Optional.empty(), result);
	}
	
	@Test
	public void testcharacter() {
		Optional<ParserResult<Character>> result = Parser.parse(Parser.character('T'), "Test");
		assertEquals('T', (char) result.get().getResult());
		
		result = Parser.parse(Parser.character('T'), "PF");
		assertEquals(Optional.empty(), result);
	}
	
	@Test
	public void testString() {
		Optional<ParserResult<String>> result = Parser.parse(Parser.string("Test"), "Test");
		assertEquals("Test", result.get().getResult());
		
		result = Parser.parse(Parser.string("Te"), "Test");
		assertEquals("Te", result.get().getResult());
		
		result = Parser.parse(Parser.string("Te"), "PF");
		assertEquals(Optional.empty(), result);
	}
	
	@Test
	public void testZeroOrPlus() {
		Optional<ParserResult<ArrayList<Character>>> result = Parser.parse(Parser.zeroOrPlus(Parser.character('a')), "aaab");
		ArrayList<Character> expected = new ArrayList<Character>();
		for(int i = 0; i < 3; i++) {
			expected.add('a');
		}
		assertEquals(expected, result.get().getResult());
		
		result = Parser.parse(Parser.zeroOrPlus(Parser.character('a')), "bbba");
		expected = new ArrayList<Character>();
		assertEquals(expected, result.get().getResult());
	}
	
	@Test
	public void testOneOrPlus() {
		Optional<ParserResult<ArrayList<Character>>> result = Parser.parse(Parser.oneOrPlus(Parser.character('a')), "aaab");
		ArrayList<Character> expected = new ArrayList<Character>();
		for(int i = 0; i < 3; i++) {
			expected.add('a');
		}
		assertEquals(expected, result.get().getResult());
		
		result = Parser.parse(Parser.oneOrPlus(Parser.character('a')), "bbba");
		assertEquals(Optional.empty(), result);
	}
}

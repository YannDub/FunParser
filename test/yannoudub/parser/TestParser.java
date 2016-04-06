package yannoudub.parser;
import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import yanndub.parser.Parser;
import yanndub.parser.ParserResult;

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
	public void testParseChar() {
		Optional<ParserResult<Character>> result = Parser.parse(Parser.parseChar('T'), "Test");
		assertEquals('T', (char) result.get().getResult());
		
		result = Parser.parse(Parser.parseChar('T'), "PF");
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
}

package yanndub.parser;

import java.util.Optional;
import java.util.function.Function;

public class Parser<T> {
	
	private Function<String, Optional<ParserResult<T>>> f;
	
	public Parser(Function<String, Optional<ParserResult<T>>> f) {
		this.f = f;
	}
	
	/**
	 * Create a new functional Parser with the function f
	 * @param f the function to apply to a String and which get an Optimal<ParserResult<T>>
	 * @return A Parser with a function with parameters String and Optimal<ParserResult<T>>
	 */
	public static <T> Parser<T> of(Function<String, Optional<ParserResult<T>>> f) {
		return new Parser<T>(f);
	}
	
	/**
	 * Parse any characters
	 * @return A Parser with result : a character
	 */
	public static Parser<Character> anyCar() {
		Function<String, Optional<ParserResult<Character>>> f = s -> {
			if(s != "") return Optional.of(new ParserResult<Character>(s.charAt(0), s.substring(1)));
			return Optional.empty();
		};
		return Parser.of(f);			
	}
	
	/**
	 * Parser which successful every time
	 * @param v the value to return
	 * @return A Parser with result : the value
	 */
	public static <T> Parser<T> succesful(T v) {
		Function<String, Optional<ParserResult<T>>> f = s -> Optional.of(new ParserResult<T>(v, s));
		return Parser.of(f);
	}
	
	/**
	 * Parser which fail every time
	 * @return A Parser with result : a character
	 */
	public static <T> Parser<T> fail() {
		return Parser.of(s -> Optional.empty());
	}
	
	public static <T, R> Parser<R> bind(Parser<T> p1, Function<T, Parser<R>> fp) {
		Function<String, Optional<ParserResult<R>>> f = s -> {
			Optional<ParserResult<T>> r = Parser.parse(p1, s);
			if(r.equals(Optional.empty())) return Optional.empty();
			return Parser.parse(fp.apply(r.get().getResult()), r.get().getToParse());
		};
		return Parser.of(f);
	}
	
	public <R> Parser<R> bind(Function<T, Parser<R>> fp) {
		return Parser.bind(this, fp);
	}
	
	public static <T> Parser<T> alternate(Parser<T> p1, Parser<T> p2) {
		Function<String, Optional<ParserResult<T>>> f = s -> {
			Optional<ParserResult<T>> r = Parser.parse(p1, s);
			if(r.equals(Optional.empty())) return Parser.parse(p2, s);
			return r;
		};
		return Parser.of(f);
	}
	
	public static Parser<Character> charCond(Function<Character, Boolean> cond) {
		return Parser.bind(Parser.anyCar(), c -> {
			if(cond.apply((Character) c)) return Parser.succesful(c);
			return Parser.fail();
		});
	}
	
	public static Parser<Character> parseChar(char c) {
		return Parser.charCond(c1 -> c1 == c);
	}
	
	public static Parser<String> string(String s) {
		if(s.equals("")) return Parser.succesful("");
		
		return Parser.bind(Parser.parseChar(s.charAt(0)), c -> Parser.string(s.substring(1))
					 .bind(cs -> Parser.succesful(c + cs)));
	}
	
	/**
	 * Launch the parser
	 * @return An Optional<ParserResult<T>> which represent the result of the parse
	 */
	public static <T> Optional<ParserResult<T>> parse(Parser<T> parser, String toParse) {
		return parser.f.apply(toParse);
	}
}

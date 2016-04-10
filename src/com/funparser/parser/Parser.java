package com.funparser.parser;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import com.funparser.utils.ParserUtils;

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
	public static Parser<Character> anyChar() {
		Function<String, Optional<ParserResult<Character>>> f = s -> {
			if(!s.equals("")) return Optional.of(new ParserResult<Character>(s.charAt(0), s.substring(1)));
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
	
	/**
	 * Bind two parser, if the first parser is a success, then function is applied and return a
	 * new parser
	 * @param p1 the first parser to bind
	 * @param fp the function to apply when p1 is a success
	 * @return the result parser after the application of fp
	 */
	public static <T, R> Parser<R> bind(Parser<T> p1, Function<T, Parser<R>> fp) {
		Function<String, Optional<ParserResult<R>>> f = s -> {
			Optional<ParserResult<T>> r = Parser.parse(p1, s);
			if(r.equals(Optional.empty())) return Optional.empty();
			return Parser.parse(fp.apply(r.get().getResult()), r.get().getToParse());
		};
		return Parser.of(f);
	}
	
	/**
	 * @see Parser#bind(Parser, Function)
	 */
	public <R> Parser<R> bind(Function<T, Parser<R>> fp) {
		return Parser.bind(this, fp);
	}
	
	/**
	 * Do one of the two parser
	 * @param p1 the first parser to do
	 * @param p2 the second parser to do if p1 was failed
	 * @return the application of one of this two parser
	 */
	public static <T> Parser<T> alternate(Parser<T> p1, Parser<T> p2) {
		Function<String, Optional<ParserResult<T>>> f = s -> {
			Optional<ParserResult<T>> r = Parser.parse(p1, s);
			if(r.equals(Optional.empty())) return Parser.parse(p2, s);
			return r;
		};
		return Parser.of(f);
	}
	
	/**
	 * @see Parser#alternate(Parser, Parser)
	 */
	public Parser<T> alternate(Parser<T> p) {
		return Parser.alternate(this, p);
	}
	
	/**
	 * A parser of character with a condition
	 * @param cond the conditional function
	 * @return a successful parser with the character of a fail, it's depend of the condition
	 */
	public static Parser<Character> charCond(Function<Character, Boolean> cond) {
		return Parser.anyChar().bind(c -> {
			if(cond.apply((Character) c)) return Parser.succesful(c);
			return Parser.fail();
		});
	}
	
	/**
	 * Parse a specific character
	 * @param c the character to parse
	 * @return a successful parser of the character of a fail parser
	 */
	public static Parser<Character> character(char c) {
		return Parser.charCond(c1 -> c1 == c);
	}
	
	/**
	 * Parse a string (without ")
	 * @param s the string to parse
	 * @return a parser result s of fail if s can't be parse
	 */
	public static Parser<String> string(String s) {
		if(s.equals("")) return Parser.succesful("");
		
		return Parser.bind(Parser.character(s.charAt(0)), c -> Parser.string(s.substring(1))
					 .bind(cs -> Parser.succesful(c + cs)));
	}
	
	/**
	 * Parse a parser and get a list of the result of this parser zero or plus times
	 * @param p the parser to parse zero or plus
	 * @return a list of the result of the parser p
	 */
	public static <T> Parser<ArrayList<T>> zeroOrPlus(Parser<T> p) {
		return Parser.oneOrPlus(p).alternate(Parser.succesful(new ArrayList<T>()));
	}
	
	/**
	 * Parse a parser and get a list of the result of this parser one or plus times
	 * @param p the parser to parse one or plus
	 * @return a list of the result of the parser p
	 */
	public static <T> Parser<ArrayList<T>> oneOrPlus(Parser<T> p) {
		return p.bind(r -> Parser.zeroOrPlus(p)
				.bind(rs -> {
					rs.add(r);
					return Parser.succesful(rs);
				}));
	}
	
	/**
	 * Parse a boolean value
	 * @return a parser of a boolean
	 */
	public static Parser<Boolean> bool() {
		return Parser.string("true").alternate(Parser.string("false"))
				.bind(s -> Parser.succesful(s.equals("true")));
	}
	
	/**
	 * Parse a digit
	 * @return a parser of a digit
	 */
	public static Parser<Character> digit() {
		return Parser.charCond(ParserUtils.isDigit());
	}
	
	/**
	 * Parse a number
	 * @return a parser of a number
	 */
	public static Parser<Integer> number() {
		return Parser.oneOrPlus(Parser.digit())
				.bind(xs -> Parser.succesful(Integer.parseInt(ParserUtils.listToString(xs))));
	}
	
	/**
	 * Launch the parser
	 * @return An Optional<ParserResult<T>> which represent the result of the parse
	 */
	public static <T> Optional<ParserResult<T>> parse(Parser<T> parser, String toParse) {
		return parser.f.apply(toParse);
	}
}

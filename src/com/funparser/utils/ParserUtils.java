package com.funparser.utils;

import java.util.ArrayList;
import java.util.function.Function;

public class ParserUtils {

	private static final String NUMBER_STRING = "0123456789";
	
	public static final Function<Character, Boolean> isDigit() {
		return c -> {
			boolean find = false;
			int i = 0;
			while(!find && i < ParserUtils.NUMBER_STRING.length()) {
				find = c == ParserUtils.NUMBER_STRING.charAt(i);
				i++;
			}
			return find;
		};
	}
	
	public static final String listToString(ArrayList<Character> list) {
		String s = "";
		for(Character c : list) {
			s = c + s;
		}
		return s;
	}
}

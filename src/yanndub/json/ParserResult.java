package yanndub.json;

public class ParserResult<T> {
	
	private T result;
	private String toParse;
	
	public ParserResult(T result, String toParse) {
		this.result = result;
		this.toParse = toParse;
	}
	
	public static <T> ParserResult<T> of(T r, String t) {
		return new ParserResult<T>(r, t);
	}
	
	public T getResult() {
		return this.result;
	}
	
	public String getToParse() {
		return this.toParse;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ParserResult) {
			ParserResult<?> r = (ParserResult<?>) obj;
			return r.result.equals(this.result) && r.toParse.equals(this.toParse);
		}
		return false;
	}
}

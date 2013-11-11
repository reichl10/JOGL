package de.joglearth.source;

public class SourceResponse<Value> {
	SourceResponseType response;
	Value value;

	public SourceResponse(SourceResponseType r, Value v) {
		response = r;
		value = v;
	}
}
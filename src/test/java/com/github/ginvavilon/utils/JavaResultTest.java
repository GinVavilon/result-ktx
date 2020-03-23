package com.github.ginvavilon.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.github.ginvavilon.utils.Result;
import com.github.ginvavilon.utils.Result.Error;
import com.github.ginvavilon.utils.Result.Ok;

public class JavaResultTest {

	private static final String STR_OK = "23";
	private static final int PARSED_OK = 23;

	private static final int OK = 2;
	private static final int ERROR = -2;
	private static final int FAIL = -1;

	public static Result<Integer, NumberFormatException> parse(String number) {
		try {
			return Result.of(Integer.parseInt(number));
		} catch (NumberFormatException e) {
			return Result.error(e);
		}
	}

	private static Integer doFun() throws IOException {
		throw new IOException("Example");
	}

	@Test
	public void testResultOf() {
		Ok<Integer, Integer> res = Result.of(OK);

		assertEquals(Integer.valueOf(OK), res.getValue());
		assertEquals(OK, res.onCatch(e -> FAIL).intValue());
	}

	@Test
	public void testResulltError() {
		Error<Integer, Integer> res = Result.error(ERROR);

		assertEquals(Integer.valueOf(ERROR), res.getError());

		assertEquals(FAIL, res.onCatch(e -> e == ERROR ? FAIL : e).intValue());
		assertEquals(FAIL, res.onCatch(e -> FAIL).intValue());
		assertEquals(ERROR, res.onCatch(e -> e).intValue());
	}

	@Test
	public void testExecuteOk() {
		int res = Result.execute(() -> 1).onCatch(e -> FAIL);
		assertEquals(1, res);
	}

	@Test
	public void testMapParseOk() {
		int res = Result.<String, NumberFormatException>of(STR_OK).map(Integer::parseInt).onCatch(error -> FAIL);
		assertEquals(PARSED_OK, res);
	}
	
	@Test
	public void testTryMap() {
		
		Integer value = Result.of(STR_OK).safetyMap(Integer::parseInt).onCatch(e -> FAIL);
		assertEquals(PARSED_OK, value.intValue());
		
		Integer valueFail = Result.of("a").safetyMap(Integer::parseInt).onCatch(e -> FAIL);
		assertEquals(FAIL, valueFail.intValue());
	}

	@Test
	public void testFlatMapParse() {

		int res = Result.of(STR_OK).flatMap(JavaResultTest::parse).onCatch(error -> FAIL);
		assertEquals(PARSED_OK, res);

		int resFail = Result.of("s").flatMap(JavaResultTest::parse).onCatch(error -> FAIL);
		assertEquals(FAIL, resFail);
	}

	@Test
	public void testExuteParseOk() {
		int result = Result.execute(() -> Integer.parseInt(STR_OK)).onCatch(e -> FAIL);
		assertEquals(PARSED_OK, result);
	}

	@Test
	public void testExuteParseFail() {
		int result = Result.execute(() -> Integer.parseInt("")).onCatch(e -> FAIL);
		assertEquals(FAIL, result);
	}

	@Test
	public void testParseOk() {
		int res = parse(STR_OK).onCatch(error -> FAIL);
		assertEquals(PARSED_OK, res);
	}

	@Test
	public void testExecuteParseFail() {
		int res = parse("s23").onCatch(error -> FAIL);
		assertEquals(FAIL, res);
	}

	@Test
	public void testExecuteFail() {
		int res = Result.execute(JavaResultTest::doFun).onCatch(e -> FAIL);
		assertEquals(FAIL, res);
	}

}

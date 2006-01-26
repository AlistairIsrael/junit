package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.description.SuiteDescription;
import org.junit.runner.description.TestDescription;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;

public class SingleMethodTest {
	public static int count;

	static public class OneTimeSetup {
		@BeforeClass
		public static void once() {
			count++;
		}

		@Test
		public void one() {
		}

		@Test
		public void two() {
		}
	}

	@Test
	public void oneTimeSetup() throws Exception {
		count = 0;
		Runner runner = Request.aMethod(OneTimeSetup.class, "one").getRunner();
		Result result = new JUnitCore().run(runner);
		
		assertEquals(1, count);
		assertEquals(1, result.getRunCount());
	}

	@RunWith(Parameterized.class)
	static public class ParameterizedOneTimeSetup {
		@Parameters
		public static Collection<Object[]> params() {
			return Parameterized.eachOne(1, 2);
		}

		public ParameterizedOneTimeSetup(int x) {
			
		}
		
		@BeforeClass
		public static void once() {
			count++;
		}

		@Test
		public void one() {
		}
	}

	@Test
	public void parameterizedOneTimeSetup() throws Exception {
		count = 0;
		Runner runner = Request.aMethod(ParameterizedOneTimeSetup.class, "one[0]").getRunner();
		Result result = new JUnitCore().run(runner);

		assertEquals(1, count);
		assertEquals(1, result.getRunCount());
	}

	@Test
	public void filteringAffectsPlan() throws Exception {
		Runner runner = Request.aMethod(OneTimeSetup.class, "one").getRunner();
		assertEquals(1, runner.testCount());
	}

	public static class TestOne {
		@Test public void a() {}
		@Test public void b() {}
	}

	public static class TestTwo {
		@Test public void a() {}
		@Test public void b() {}
	}
	
	@RunWith(Suite.class)
	@SuiteClasses({TestOne.class, TestTwo.class})
	public static class OneTwoSuite {} 

	@Test
	public void eliminateUnnecessaryTreeBranches() throws Exception {
		Runner runner = Request.aClass(OneTwoSuite.class).filterWith(new TestDescription(TestOne.class, "a")).getRunner();
		SuiteDescription description = (SuiteDescription) runner.getDescription();
		assertEquals(1, description.getChildren().size());
	}
}
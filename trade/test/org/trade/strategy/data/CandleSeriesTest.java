package org.trade.strategy.data;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.TradestrategyTest;
import org.trade.ui.TradeAppLoadConfig;

public class CandleSeriesTest extends TestCase {

	private final static Logger _log = LoggerFactory
			.getLogger(CandleSeriesTest.class);

	private String symbol = "TEST";
	private Tradestrategy tradestrategy = null;

	/**
	 * Method setUp.
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		try {
			TradeAppLoadConfig.loadAppProperties();
			this.tradestrategy = TradestrategyTest.getTestTradestrategy(symbol);
			TestCase.assertNotNull(this.tradestrategy);
		} catch (Exception e) {
			TestCase.fail("Error on setup " + e.getMessage());
		}
	}

	/**
	 * Method tearDown.
	 * 
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		TradestrategyTest.clearDBData();
	}

	@Test
	public void testCandleSeriessClone() {
		try {

			CandleSeries candleSeries = this.tradestrategy.getStrategyData()
					.getBaseCandleSeries();
			CandleSeries series = (CandleSeries) this.tradestrategy
					.getStrategyData().getBaseCandleSeries().clone();
			if (candleSeries.equals(series)) {
				_log.info("CandleSeries: " + series.toString());
			}
			TestCase.assertEquals(series, candleSeries);

		} catch (Exception e) {
			TestCase.fail("Error on testCandleSeriessClone " + e.getMessage());
		}
	}

}

/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.persistent.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectHome;
import org.trade.core.dao.Aspects;
import org.trade.core.util.TradingCalendar;
import org.trade.dictionary.valuetype.AccountType;
import org.trade.dictionary.valuetype.BarSize;
import org.trade.dictionary.valuetype.ChartDays;
import org.trade.dictionary.valuetype.Currency;
import org.trade.dictionary.valuetype.DAOPortfolio;
import org.trade.dictionary.valuetype.DAOStrategy;
import org.trade.dictionary.valuetype.Exchange;
import org.trade.dictionary.valuetype.SECType;
import org.trade.dictionary.valuetype.TradestrategyStatus;
import org.trade.ui.TradeAppLoadConfig;

/**
 */
public class TradestrategyTest extends TestCase {

	private final static Logger _log = LoggerFactory
			.getLogger(TradestrategyTest.class);

	private String symbol = "TEST";
	private TradestrategyHome tradestrategyHome = null;

	/**
	 * Method setUp.
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		TradeAppLoadConfig.loadAppProperties();
		tradestrategyHome = new TradestrategyHome();
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
	public void testFindVersionById() {
		try {
			Tradestrategy tradestrategy = TradestrategyTest
					.getTestTradestrategy(symbol);
			TestCase.assertNotNull(tradestrategy);

			Integer version = tradestrategyHome.findVersionById(tradestrategy
					.getIdTradeStrategy());
			TestCase.assertNotNull(version);
			_log.info("testFindVersionById IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy() + " version: "
					+ version);

		} catch (Exception e) {
			TestCase.fail("Error testAddTradestrategy Msg: " + e.getMessage());
		}
	}

	@Test
	public void testFindPositionOrdersById() {
		try {
			Tradestrategy tradestrategy = TradestrategyTest
					.getTestTradestrategy(symbol);
			TestCase.assertNotNull(tradestrategy);
			_log.info("testTradingdaysSave IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy());

			TradestrategyOrders positionOrders = tradestrategyHome
					.findPositionOrdersByTradestrategyId(tradestrategy
							.getIdTradeStrategy());
			TestCase.assertNotNull(positionOrders);
			_log.info("testTradingdaysSave PositionOrders IdTradeStrategy:"
					+ positionOrders.getIdTradeStrategy() + "found.");
			positionOrders.setStatus(TradestrategyStatus.CANCELLED);
			AspectHome aspectHome = new AspectHome();
			positionOrders = aspectHome.persist(positionOrders);
			positionOrders = tradestrategyHome
					.findPositionOrdersByTradestrategyId(tradestrategy
							.getIdTradeStrategy());
			_log.info("testTradingdaysSave PositionOrders IdTradeStrategy:"
					+ positionOrders.getIdTradeStrategy() + "found Status: "
					+ positionOrders.getStatus());
			TestCase.assertEquals(TradestrategyStatus.CANCELLED,
					positionOrders.getStatus());

		} catch (Exception e) {
			TestCase.fail("Error testAddTradestrategy Msg: " + e.getMessage());
		}
	}

	@Test
	public void testAddTradestrategy() {

		try {

			Tradestrategy tradestrategy = TradestrategyTest
					.getTestTradestrategy(symbol);
			TestCase.assertNotNull(tradestrategy);
			_log.info("testTradingdaysSave IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy());
			tradestrategy = tradestrategyHome.findById(tradestrategy
					.getIdTradeStrategy());
			TestCase.assertNotNull(tradestrategy);
			_log.info("testTradingdaysSave IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy() + "found.");

		} catch (Exception e) {
			TestCase.fail("Error testAddTradestrategy Msg: " + e.getMessage());
		}
	}

	@Test
	public void testUpdateTradeStrategy() {

		try {
			Date open = TradingCalendar.getBusinessDayStart(TradingCalendar
					.getMostRecentTradingDay(new Date()));
			TradingdayHome tradingdayHome = new TradingdayHome();
			Tradingdays tradingdays = tradingdayHome
					.findTradingdaysByDateRange(open, open);
			for (Tradingday tradingday : tradingdays.getTradingdays()) {
				for (Tradestrategy tradestrategy : tradingday
						.getTradestrategies()) {
					tradestrategy.setStatus(TradestrategyStatus.OPEN);
				}
				tradingdayHome.persist(tradingday);

				for (Tradestrategy tradestrategy : tradingday
						.getTradestrategies()) {

					_log.info("testTradingdaysUpdate IdTradeStrategy:"
							+ tradestrategy.getIdTradeStrategy() + "  Status: "
							+ tradestrategy.getStatus());
					TestCase.assertEquals(TradestrategyStatus.OPEN,
							tradestrategy.getStatus());
				}
			}
		} catch (Exception e) {
			TestCase.fail("Error update row " + e.getMessage());
		}
	}

	@Test
	public void testReadAndSavefileMultipleDayTradestrategy() {

		try {
			AspectHome aspectHome = new AspectHome();
			TradingdayHome tradingdayHome = new TradingdayHome();
			Tradingdays tradingdays = new Tradingdays();
			Tradingday instance = Tradingday.newInstance(TradingCalendar
					.getMostRecentTradingDay(new Date()));
			tradingdays.add(instance);

			String fileName = "db/LoadFile10Stocks.csv";
			tradingdays.populateDataFromFile(fileName, instance);
			TestCase.assertFalse(tradingdays.getTradingdays().isEmpty());
			for (Tradingday tradingday : tradingdays.getTradingdays()) {
				tradingdayHome.persist(tradingday);
				for (Tradestrategy tradestrategy : tradingday
						.getTradestrategies()) {
					_log.info("testTradingdaysUpdate IdTradeStrategy:"
							+ tradestrategy.getIdTradeStrategy());
					aspectHome.remove(tradestrategy);
					aspectHome.remove(tradestrategy.getContract());

				}
				aspectHome.remove(tradingday);
			}
		} catch (Exception e) {
			TestCase.fail("Error adding row " + e.getMessage());
		}
	}

	@Test
	public void testReadAndSavefileOneDayTradestrategy() {

		try {
			AspectHome aspectHome = new AspectHome();
			TradingdayHome tradingdayHome = new TradingdayHome();
			Tradingdays tradingdays = new Tradingdays();
			Tradingday instance = Tradingday.newInstance(TradingCalendar
					.getMostRecentTradingDay(new Date()));
			tradingdays.add(instance);

			String fileName = "db/LoadFile1Stock.csv";

			tradingdays.populateDataFromFile(fileName, instance);
			TestCase.assertFalse(tradingdays.getTradingdays().isEmpty());
			for (Tradingday tradingday : tradingdays.getTradingdays()) {
				tradingdayHome.persist(tradingday);
				for (Tradestrategy tradestrategy : tradingday
						.getTradestrategies()) {

					_log.info("testTradingdaysUpdate IdTradeStrategy:"
							+ tradestrategy.getIdTradeStrategy());
					aspectHome.remove(tradestrategy);
					aspectHome.remove(tradestrategy.getContract());
				}
				aspectHome.remove(tradingday);
			}

		} catch (Exception e) {
			TestCase.fail("Error adding row " + e.getMessage());
		}
	}

	/**
	 * Method getTestTradestrategy.
	 * 
	 * @return Tradestrategy
	 * @throws Exception
	 */
	public static Tradestrategy getTestTradestrategy(String symbol)
			throws Exception {
		ContractHome contractHome = new ContractHome();
		PortfolioHome portfolioHome = new PortfolioHome();

		TradestrategyHome tradestrategyHome = new TradestrategyHome();
		AspectHome aspectHome = new AspectHome();

		Tradestrategy tradestrategy = null;
		Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
		Portfolio portfolio = (Portfolio) DAOPortfolio.newInstance()
				.getObject();
		portfolio = portfolioHome.findByName(portfolio.getName());
		if (portfolio.getPortfolioAccounts().isEmpty()) {
			Account account = new Account("Test", "T123456", Currency.USD,
					AccountType.INDIVIDUAL);
			account.setAvailableFunds(new BigDecimal(25000));
			account.setBuyingPower(new BigDecimal(100000));
			account.setCashBalance(new BigDecimal(25000));
			PortfolioAccount portfolioAccount = new PortfolioAccount(portfolio,
					account);
			portfolio.getPortfolioAccounts().add(portfolioAccount);
			portfolio = aspectHome.persist(portfolio);
		}
		Date open = TradingCalendar.getBusinessDayStart(TradingCalendar
				.getMostRecentTradingDay(new Date()));

		Contract contract = contractHome.findByUniqueKey(SECType.STOCK, symbol,
				Exchange.SMART, Currency.USD, null);
		if (null == contract) {
			contract = new Contract(SECType.STOCK, symbol, Exchange.SMART,
					Currency.USD, null, null);
			contract = aspectHome.persist(contract);

		} else {
			tradestrategy = tradestrategyHome.findTradestrategyByUniqueKeys(
					open, strategy.getName(), contract.getIdContract(),
					portfolio.getName());
			if (null != tradestrategy) {
				Tradestrategy transientInstance = tradestrategyHome
						.findById(tradestrategy.getIdTradeStrategy());
				transientInstance.setStatus(null);
				aspectHome.persist(transientInstance);

				Hashtable<Integer, TradePosition> tradePositions = new Hashtable<Integer, TradePosition>();
				for (TradeOrder tradeOrder : transientInstance.getTradeOrders()) {
					if (tradeOrder.hasTradePosition())
						tradePositions.put(tradeOrder.getTradePosition()
								.getIdTradePosition(), tradeOrder
								.getTradePosition());

					if (null != tradeOrder.getIdTradeOrder()) {
						aspectHome.remove(tradeOrder);
					}
				}

				for (TradePosition tradePosition : tradePositions.values()) {
					tradePosition = (TradePosition) aspectHome
							.findById(tradePosition);
					/*
					 * Remove the open trade position from contract if this is a
					 * tradePosition to be deleted.
					 */
					if (tradePosition.equals(transientInstance.getContract()
							.getTradePosition())) {
						transientInstance.getContract().setTradePosition(null);
						aspectHome.persist(transientInstance.getContract());
					}
					aspectHome.remove(tradePosition);
				}

				transientInstance.getTradeOrders().clear();
				return transientInstance;
			}
		}
		TradingdayHome tradingdayHome = new TradingdayHome();
		Tradingday tradingday = Tradingday.newInstance(open);
		Tradingday instance = tradingdayHome.findByOpenCloseDate(
				tradingday.getOpen(), tradingday.getClose());
		if (null != instance) {
			tradingday.getTradestrategies().clear();
			tradingday = instance;
		}
		tradestrategy = new Tradestrategy(contract, tradingday, strategy,
				portfolio, new BigDecimal(100), "BUY", "0", true,
				ChartDays.TWO_DAYS, BarSize.FIVE_MIN);
		tradingday.addTradestrategy(tradestrategy);
		tradingdayHome.persist(tradingday);
		return tradestrategyHome.findById(tradestrategy.getIdTradeStrategy());
	}

	/**
	 * Method clearDBData.
	 * 
	 * @throws Exception
	 */
	public static void clearDBData() throws Exception {

		AspectHome aspectHome = new AspectHome();
		Aspects contracts = aspectHome
				.findByClassName(Contract.class.getName());
		for (Aspect aspect : contracts.getAspect()) {
			((Contract) aspect).setTradePosition(null);
			aspectHome.persist(aspect);
		}

		Aspects tradeOrders = aspectHome.findByClassName(TradeOrder.class
				.getName());
		for (Aspect aspect : tradeOrders.getAspect()) {
			aspectHome.remove(aspect);
		}

		Aspects tradePositions = aspectHome.findByClassName(TradePosition.class
				.getName());
		for (Aspect aspect : tradePositions.getAspect()) {
			aspectHome.remove(aspect);
		}
		Aspects portfolioAccounts = aspectHome
				.findByClassName(PortfolioAccount.class.getName());
		for (Aspect aspect : portfolioAccounts.getAspect()) {
			aspectHome.remove(aspect);
		}
		Aspects accounts = aspectHome.findByClassName(Account.class.getName());
		for (Aspect aspect : accounts.getAspect()) {
			aspectHome.remove(aspect);
		}
		Aspects tradestrategies = aspectHome
				.findByClassName(Tradestrategy.class.getName());
		for (Aspect aspect : tradestrategies.getAspect()) {
			aspectHome.remove(aspect);
		}
		contracts = aspectHome.findByClassName(Contract.class.getName());
		for (Aspect aspect : contracts.getAspect()) {
			aspectHome.remove(aspect);
		}
		Aspects tradingdays = aspectHome.findByClassName(Tradingday.class
				.getName());
		for (Aspect aspect : tradingdays.getAspect()) {
			aspectHome.remove(aspect);
		}
	}

	@Test
	public void testFindTradestrategyDistinctByDateRange() {
		try {
			Tradestrategy tradestrategy = TradestrategyTest
					.getTestTradestrategy(symbol);
			TestCase.assertNotNull(tradestrategy);
			_log.info("testTradingdaysSave IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy());
			List<Tradestrategy> results = tradestrategyHome
					.findTradestrategyDistinctByDateRange(tradestrategy
							.getTradingday().getOpen(), tradestrategy
							.getTradingday().getOpen());
			for (Tradestrategy value : results) {
				_log.info("BarSize: " + value.getBarSize() + " ChartDays: "
						+ value.getChartDays() + " Strategy: "
						+ value.getStrategy().getName());
			}
			TestCase.assertNotNull(results);

		} catch (Exception e) {
			TestCase.fail("Error testAddTradestrategy Msg: " + e.getMessage());
		}
	}

	@Test
	public void testFindTradestrategyContractDistinctByDateRange() {
		try {
			Tradestrategy tradestrategy = TradestrategyTest
					.getTestTradestrategy(symbol);
			TestCase.assertNotNull(tradestrategy);
			_log.info("testTradingdaysSave IdTradeStrategy:"
					+ tradestrategy.getIdTradeStrategy());
			List<Tradestrategy> results = tradestrategyHome
					.findTradestrategyContractDistinctByDateRange(tradestrategy
							.getTradingday().getOpen(), tradestrategy
							.getTradingday().getOpen());
			for (Tradestrategy value : results) {
				_log.info("Contract: " + value.getContract().getSymbol());
			}
			TestCase.assertNotNull(results);

		} catch (Exception e) {
			TestCase.fail("Error testAddTradestrategy Msg: " + e.getMessage());
		}
	}
}

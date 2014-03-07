select
cast(rand()*1000000000 as unsigned integer) as idTradelogSummary,
dataAll.period as period,
(dataAll.winCount/ (dataAll.winCount  + dataAll.lossCount)) as battingAverage,
((dataAll.profitAmount/ dataAll.winCount)/((dataAll.lossAmount*-1)/dataAll.lossCount))  as simpleSharpeRatio,
cast(dataAll.quantity as signed integer) as quantity,
dataAll.commission as commission,
(dataAll.profitAmount + dataAll.lossAmount) as grossProfitLoss,
(dataAll.profitAmount + dataAll.lossAmount - dataAll.commission) as netProfitLoss,
dataAll.profitAmount as profitAmount,
dataAll.lossAmount as lossAmount,
cast(dataAll.winCount as signed integer)  as winCount,
cast(dataAll.lossCount as signed integer)  as lossCount,
cast(dataAll.positionCount as signed integer)  as positionCount,
cast(dataAll.tradestrategyCount as signed integer)  as tradestrategyCount
from (select
dataC.period as period,
sum(dataC.quantity) as quantity,
sum(dataC.commission) as commission,
sum(dataC.profitAmount) as profitAmount,
sum(dataC.lossAmount) as lossAmount,
sum(dataC.winCount) as winCount,
sum(dataC.lossCount) as lossCount,
sum(dataC.positionCount) as positionCount,
sum(dataC.tradestrategyCount) as tradestrategyCount
from(select
'Total' as period,
sum(dataA.quantity) as quantity,
sum(dataA.commission) as commission,
sum(dataA.profitAmount) as profitAmount,
sum(dataA.lossAmount) as lossAmount,
sum(dataA.winCount) as winCount,
sum(dataA.lossCount) as lossCount,
sum(dataA.positionCount) as positionCount,
sum(dataA.tradestrategyCount) as tradestrategyCount
from (select
dataD.period as period,
dataD.quantityTotal as quantity,
dataD.commission as commission,
if(dataD.quantity = 0 , dataD.profitAmount, 0) as profitAmount,
if(dataD.quantity = 0 , dataD.lossAmount, 0) as lossAmount,
if(dataD.quantity = 0 , dataD.winCount, 0) as winCount,
if(dataD.quantity = 0 , dataD.lossCount, 0) as lossCount,
dataD.positionCount as positionCount,
dataD.tradestrategyCount as tradestrategyCount
from(select
date_format(tradeposition.positionCloseDate , '%Y/%m') as period,
contract.symbol,
tradeposition.idTradeposition,
sum(ifnull(tradeorder.quantity,0))  as quantityTotal,
sum((if(tradeorder.action = 'BUY',  1 , -1)) * ifnull(tradeorder.quantity,0))  as quantity,
sum(ifnull(tradeorder.commission,0)) as commission,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)) > 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)), 0)	as profitAmount,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)) < 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)), 0)	as lossAmount,
(if((:winLossAmount) < (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice))), 1 ,0 )) as winCount,
(if((-1*:winLossAmount) >= (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice))), 1 ,0 )) as lossCount,
if(ifnull(tradeposition.idTradePosition,0),1, 0) as positionCount,
0 as tradestrategyCount
from contract
left outer join tradeposition  on contract.idContract = tradeposition.idContract
left outer join tradeorder  on tradeposition.idTradePosition = tradeorder.idTradePosition
inner join tradestrategy on tradestrategy.idTradestrategy = tradeorder.idTradestrategy
inner join portfolio on tradestrategy.idPortfolio = portfolio.idPortfolio
where tradeorder.isFilled =1
and tradeposition.openQuantity = 0
and tradestrategy.trade = 1
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.positionCloseDate between :start and :end
and portfolio.idPortfolio = :idPortfolio
group by
period,
contract.symbol,
tradeposition.idTradeposition
union all
select
date_format(tradingday.open , '%Y/%m') as period,
contract.symbol,
0 as idTradePosition,
0 as quantityTotal,
0 as quantity,
0 as commission,
0 as profitAmount,
0 as lossAmount,
0 as winCount,
0 as lossCount,
0 as positionCount,
if(ifnull(tradestrategy.idTradestrategy,0),1, 0)  as tradestrategyCount
from tradestrategy
inner join contract  on contract.idContract = tradestrategy.idContract
inner join tradingday  on tradingday.idTradingday = tradestrategy.idTradingday
inner join portfolio on tradestrategy.idPortfolio = portfolio.idPortfolio
where tradingday.open between :start and :end
and (isnull(:symbol) or contract.symbol = :symbol)
and (portfolio.idPortfolio = :idPortfolio or portfolio.idPortfolio is null)
group by
period,
contract.symbol,
tradestrategy.idTradestrategy) dataD) dataA
group by dataA.period) dataC
group by
dataC.period
union all
select
dataM.period as period,
sum(dataM.quantity) as quantity,
sum(dataM.commission) as commission,
sum(dataM.profitAmount) as profitAmount,
sum(dataM.lossAmount) as lossAmount,
sum(dataM.winCount) as winCount,
sum(dataM.lossCount) as lossCount,
sum(dataM.positionCount) as positionCount,
sum(dataM.tradestrategyCount) as tradestrategyCount
from (select
dataD.period as period,
dataD.quantityTotal as quantity,
dataD.commission as commission,
if(dataD.quantity = 0 , dataD.profitAmount, 0) as profitAmount,
if(dataD.quantity = 0 , dataD.lossAmount, 0) as lossAmount,
if(dataD.quantity = 0 , dataD.winCount, 0) as winCount,
if(dataD.quantity = 0 , dataD.lossCount, 0) as lossCount,
dataD.positionCount as positionCount,
dataD.tradestrategyCount as tradestrategyCount
from(select
date_format(tradeposition.positionCloseDate , '%Y/%m') as period,
contract.symbol,
tradeposition.idTradePosition,
sum(ifnull(tradeorder.quantity,0))  as quantityTotal,
sum((if( tradeorder.action = 'BUY',  1 , -1)) * ifnull(tradeorder.quantity,0))  as quantity,
sum(ifnull(tradeorder.commission,0)) as commission,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)) > 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)), 0)	as profitAmount,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)) < 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice)), 0)	as lossAmount,
(if((:winLossAmount) < (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice))), 1 ,0 )) as winCount,
(if((-1*:winLossAmount) >= (sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.averageFilledPrice))), 1 ,0 )) as lossCount,
if(ifnull(tradeposition.idTradePosition,0),1, 0) as positionCount,
0 as tradestrategyCount
from contract
left outer join tradeposition  on contract.idContract = tradeposition.idContract
left outer join tradeorder  on tradeposition.idTradePosition = tradeorder.idTradePosition
inner join tradestrategy on tradestrategy.idTradestrategy = tradeorder.idTradestrategy
inner join portfolio on tradestrategy.idPortfolio = portfolio.idPortfolio
where tradeorder.isFilled =1
and tradeposition.openQuantity = 0
and tradestrategy.trade = 1
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.positionCloseDate between :start and :end
and portfolio.idPortfolio = :idPortfolio
group by
period,
contract.symbol,
tradeposition.idTradePosition
union all
select date_format(tradingday.open , '%Y/%m') as period,
contract.symbol,
0 as idTradePosition,
0 as quantityTotal,
0 as quantity,
0 as commission,
0 as profitAmount,
0 as lossAmount,
0 as winCount,
0 as lossCount,
0 as positionCount,
if(ifnull(tradestrategy.idTradestrategy,0),1, 0)  as tradestrategyCount
from tradestrategy
inner join contract  on contract.idContract = tradestrategy.idContract
inner join tradingday  on tradingday.idTradingday = tradestrategy.idTradingday
inner join portfolio on tradestrategy.idPortfolio = portfolio.idPortfolio
where tradingday.open between :start and :end
and (isnull(:symbol) or contract.symbol = :symbol)
and (portfolio.idPortfolio = :idPortfolio or portfolio.idPortfolio is null)
group by
period,
contract.symbol,
tradestrategy.idTradestrategy) dataD) dataM
group by dataM.period) dataAll
order by dataAll.period desc

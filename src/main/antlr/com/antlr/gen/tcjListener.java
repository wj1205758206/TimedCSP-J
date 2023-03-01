// Generated from java-escape by ANTLR 4.11.1
package com.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tcjParser}.
 */
public interface tcjListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tcjParser#specification}.
	 * @param ctx the parse tree
	 */
	void enterSpecification(tcjParser.SpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#specification}.
	 * @param ctx the parse tree
	 */
	void exitSpecification(tcjParser.SpecificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#specBody}.
	 * @param ctx the parse tree
	 */
	void enterSpecBody(tcjParser.SpecBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#specBody}.
	 * @param ctx the parse tree
	 */
	void exitSpecBody(tcjParser.SpecBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#library}.
	 * @param ctx the parse tree
	 */
	void enterLibrary(tcjParser.LibraryContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#library}.
	 * @param ctx the parse tree
	 */
	void exitLibrary(tcjParser.LibraryContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#channel}.
	 * @param ctx the parse tree
	 */
	void enterChannel(tcjParser.ChannelContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#channel}.
	 * @param ctx the parse tree
	 */
	void exitChannel(tcjParser.ChannelContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#assertion}.
	 * @param ctx the parse tree
	 */
	void enterAssertion(tcjParser.AssertionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#assertion}.
	 * @param ctx the parse tree
	 */
	void exitAssertion(tcjParser.AssertionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#withClause}.
	 * @param ctx the parse tree
	 */
	void enterWithClause(tcjParser.WithClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#withClause}.
	 * @param ctx the parse tree
	 */
	void exitWithClause(tcjParser.WithClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#definitionRef}.
	 * @param ctx the parse tree
	 */
	void enterDefinitionRef(tcjParser.DefinitionRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#definitionRef}.
	 * @param ctx the parse tree
	 */
	void exitDefinitionRef(tcjParser.DefinitionRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#alphabet}.
	 * @param ctx the parse tree
	 */
	void enterAlphabet(tcjParser.AlphabetContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#alphabet}.
	 * @param ctx the parse tree
	 */
	void exitAlphabet(tcjParser.AlphabetContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#define}.
	 * @param ctx the parse tree
	 */
	void enterDefine(tcjParser.DefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#define}.
	 * @param ctx the parse tree
	 */
	void exitDefine(tcjParser.DefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#dparameter}.
	 * @param ctx the parse tree
	 */
	void enterDparameter(tcjParser.DparameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#dparameter}.
	 * @param ctx the parse tree
	 */
	void exitDparameter(tcjParser.DparameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#dstatement}.
	 * @param ctx the parse tree
	 */
	void enterDstatement(tcjParser.DstatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#dstatement}.
	 * @param ctx the parse tree
	 */
	void exitDstatement(tcjParser.DstatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(tcjParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(tcjParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(tcjParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(tcjParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration(tcjParser.LocalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration(tcjParser.LocalVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(tcjParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(tcjParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(tcjParser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(tcjParser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(tcjParser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(tcjParser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#conditionalXorExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalXorExpression(tcjParser.ConditionalXorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#conditionalXorExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalXorExpression(tcjParser.ConditionalXorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#indexedExpression}.
	 * @param ctx the parse tree
	 */
	void enterIndexedExpression(tcjParser.IndexedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#indexedExpression}.
	 * @param ctx the parse tree
	 */
	void exitIndexedExpression(tcjParser.IndexedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#bitwiseLogicExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseLogicExpression(tcjParser.BitwiseLogicExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#bitwiseLogicExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseLogicExpression(tcjParser.BitwiseLogicExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(tcjParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(tcjParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(tcjParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(tcjParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(tcjParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(tcjParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(tcjParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(tcjParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(tcjParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(tcjParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#arrayExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpression(tcjParser.ArrayExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#arrayExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpression(tcjParser.ArrayExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpressionNotPlusMinus(tcjParser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpressionNotPlusMinus(tcjParser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#methods_fields_call}.
	 * @param ctx the parse tree
	 */
	void enterMethods_fields_call(tcjParser.Methods_fields_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#methods_fields_call}.
	 * @param ctx the parse tree
	 */
	void exitMethods_fields_call(tcjParser.Methods_fields_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#letDefintion}.
	 * @param ctx the parse tree
	 */
	void enterLetDefintion(tcjParser.LetDefintionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#letDefintion}.
	 * @param ctx the parse tree
	 */
	void exitLetDefintion(tcjParser.LetDefintionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#variableRange}.
	 * @param ctx the parse tree
	 */
	void enterVariableRange(tcjParser.VariableRangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#variableRange}.
	 * @param ctx the parse tree
	 */
	void exitVariableRange(tcjParser.VariableRangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#argumentExpression}.
	 * @param ctx the parse tree
	 */
	void enterArgumentExpression(tcjParser.ArgumentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#argumentExpression}.
	 * @param ctx the parse tree
	 */
	void exitArgumentExpression(tcjParser.ArgumentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#ifExpression}.
	 * @param ctx the parse tree
	 */
	void enterIfExpression(tcjParser.IfExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#ifExpression}.
	 * @param ctx the parse tree
	 */
	void exitIfExpression(tcjParser.IfExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#whileExpression}.
	 * @param ctx the parse tree
	 */
	void enterWhileExpression(tcjParser.WhileExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#whileExpression}.
	 * @param ctx the parse tree
	 */
	void exitWhileExpression(tcjParser.WhileExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#recordExpression}.
	 * @param ctx the parse tree
	 */
	void enterRecordExpression(tcjParser.RecordExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#recordExpression}.
	 * @param ctx the parse tree
	 */
	void exitRecordExpression(tcjParser.RecordExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#recordElement}.
	 * @param ctx the parse tree
	 */
	void enterRecordElement(tcjParser.RecordElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#recordElement}.
	 * @param ctx the parse tree
	 */
	void exitRecordElement(tcjParser.RecordElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#definition}.
	 * @param ctx the parse tree
	 */
	void enterDefinition(tcjParser.DefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#definition}.
	 * @param ctx the parse tree
	 */
	void exitDefinition(tcjParser.DefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#interleaveExpr}.
	 * @param ctx the parse tree
	 */
	void enterInterleaveExpr(tcjParser.InterleaveExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#interleaveExpr}.
	 * @param ctx the parse tree
	 */
	void exitInterleaveExpr(tcjParser.InterleaveExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#parallelExpr}.
	 * @param ctx the parse tree
	 */
	void enterParallelExpr(tcjParser.ParallelExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#parallelExpr}.
	 * @param ctx the parse tree
	 */
	void exitParallelExpr(tcjParser.ParallelExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#paralDef}.
	 * @param ctx the parse tree
	 */
	void enterParalDef(tcjParser.ParalDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#paralDef}.
	 * @param ctx the parse tree
	 */
	void exitParalDef(tcjParser.ParalDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#paralDef2}.
	 * @param ctx the parse tree
	 */
	void enterParalDef2(tcjParser.ParalDef2Context ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#paralDef2}.
	 * @param ctx the parse tree
	 */
	void exitParalDef2(tcjParser.ParalDef2Context ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#internalChoiceExpr}.
	 * @param ctx the parse tree
	 */
	void enterInternalChoiceExpr(tcjParser.InternalChoiceExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#internalChoiceExpr}.
	 * @param ctx the parse tree
	 */
	void exitInternalChoiceExpr(tcjParser.InternalChoiceExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#externalChoiceExpr}.
	 * @param ctx the parse tree
	 */
	void enterExternalChoiceExpr(tcjParser.ExternalChoiceExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#externalChoiceExpr}.
	 * @param ctx the parse tree
	 */
	void exitExternalChoiceExpr(tcjParser.ExternalChoiceExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#interruptExpr}.
	 * @param ctx the parse tree
	 */
	void enterInterruptExpr(tcjParser.InterruptExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#interruptExpr}.
	 * @param ctx the parse tree
	 */
	void exitInterruptExpr(tcjParser.InterruptExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#hidingExpr}.
	 * @param ctx the parse tree
	 */
	void enterHidingExpr(tcjParser.HidingExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#hidingExpr}.
	 * @param ctx the parse tree
	 */
	void exitHidingExpr(tcjParser.HidingExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#sequentialExpr}.
	 * @param ctx the parse tree
	 */
	void enterSequentialExpr(tcjParser.SequentialExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#sequentialExpr}.
	 * @param ctx the parse tree
	 */
	void exitSequentialExpr(tcjParser.SequentialExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#guardExpr}.
	 * @param ctx the parse tree
	 */
	void enterGuardExpr(tcjParser.GuardExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#guardExpr}.
	 * @param ctx the parse tree
	 */
	void exitGuardExpr(tcjParser.GuardExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#timeoutExpr}.
	 * @param ctx the parse tree
	 */
	void enterTimeoutExpr(tcjParser.TimeoutExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#timeoutExpr}.
	 * @param ctx the parse tree
	 */
	void exitTimeoutExpr(tcjParser.TimeoutExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#withinExpr}.
	 * @param ctx the parse tree
	 */
	void enterWithinExpr(tcjParser.WithinExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#withinExpr}.
	 * @param ctx the parse tree
	 */
	void exitWithinExpr(tcjParser.WithinExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#deadlineExpr}.
	 * @param ctx the parse tree
	 */
	void enterDeadlineExpr(tcjParser.DeadlineExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#deadlineExpr}.
	 * @param ctx the parse tree
	 */
	void exitDeadlineExpr(tcjParser.DeadlineExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#waituntilExpr}.
	 * @param ctx the parse tree
	 */
	void enterWaituntilExpr(tcjParser.WaituntilExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#waituntilExpr}.
	 * @param ctx the parse tree
	 */
	void exitWaituntilExpr(tcjParser.WaituntilExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#channelExpr}.
	 * @param ctx the parse tree
	 */
	void enterChannelExpr(tcjParser.ChannelExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#channelExpr}.
	 * @param ctx the parse tree
	 */
	void exitChannelExpr(tcjParser.ChannelExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#eventExpr}.
	 * @param ctx the parse tree
	 */
	void enterEventExpr(tcjParser.EventExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#eventExpr}.
	 * @param ctx the parse tree
	 */
	void exitEventExpr(tcjParser.EventExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#caseExpr}.
	 * @param ctx the parse tree
	 */
	void enterCaseExpr(tcjParser.CaseExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#caseExpr}.
	 * @param ctx the parse tree
	 */
	void exitCaseExpr(tcjParser.CaseExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#caseCondition}.
	 * @param ctx the parse tree
	 */
	void enterCaseCondition(tcjParser.CaseConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#caseCondition}.
	 * @param ctx the parse tree
	 */
	void exitCaseCondition(tcjParser.CaseConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void enterIfExpr(tcjParser.IfExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void exitIfExpr(tcjParser.IfExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#ifExprs}.
	 * @param ctx the parse tree
	 */
	void enterIfExprs(tcjParser.IfExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#ifExprs}.
	 * @param ctx the parse tree
	 */
	void exitIfExprs(tcjParser.IfExprsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void enterIfBlock(tcjParser.IfBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void exitIfBlock(tcjParser.IfBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#atomicExpr}.
	 * @param ctx the parse tree
	 */
	void enterAtomicExpr(tcjParser.AtomicExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#atomicExpr}.
	 * @param ctx the parse tree
	 */
	void exitAtomicExpr(tcjParser.AtomicExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(tcjParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(tcjParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#eventM}.
	 * @param ctx the parse tree
	 */
	void enterEventM(tcjParser.EventMContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#eventM}.
	 * @param ctx the parse tree
	 */
	void exitEventM(tcjParser.EventMContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#eventList}.
	 * @param ctx the parse tree
	 */
	void enterEventList(tcjParser.EventListContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#eventList}.
	 * @param ctx the parse tree
	 */
	void exitEventList(tcjParser.EventListContext ctx);
	/**
	 * Enter a parse tree produced by {@link tcjParser#eventName}.
	 * @param ctx the parse tree
	 */
	void enterEventName(tcjParser.EventNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link tcjParser#eventName}.
	 * @param ctx the parse tree
	 */
	void exitEventName(tcjParser.EventNameContext ctx);
}
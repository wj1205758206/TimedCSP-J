// Generated from java-escape by ANTLR 4.11.1
package com.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link tcjParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface tcjVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link tcjParser#specification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecification(tcjParser.SpecificationContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#specBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecBody(tcjParser.SpecBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#library}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLibrary(tcjParser.LibraryContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#channel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChannel(tcjParser.ChannelContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#assertion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertion(tcjParser.AssertionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#withClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithClause(tcjParser.WithClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#definitionRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitionRef(tcjParser.DefinitionRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#alphabet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlphabet(tcjParser.AlphabetContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#define}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefine(tcjParser.DefineContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#dparameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDparameter(tcjParser.DparameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#dstatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDstatement(tcjParser.DstatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(tcjParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(tcjParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariableDeclaration(tcjParser.LocalVariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(tcjParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalOrExpression(tcjParser.ConditionalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalAndExpression(tcjParser.ConditionalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#conditionalXorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalXorExpression(tcjParser.ConditionalXorExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#indexedExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexedExpression(tcjParser.IndexedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#bitwiseLogicExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseLogicExpression(tcjParser.BitwiseLogicExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(tcjParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(tcjParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(tcjParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(tcjParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(tcjParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#arrayExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpression(tcjParser.ArrayExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpressionNotPlusMinus(tcjParser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#methods_fields_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethods_fields_call(tcjParser.Methods_fields_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#letDefintion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetDefintion(tcjParser.LetDefintionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#variableRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableRange(tcjParser.VariableRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#argumentExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentExpression(tcjParser.ArgumentExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#ifExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExpression(tcjParser.IfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#whileExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileExpression(tcjParser.WhileExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#recordExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordExpression(tcjParser.RecordExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#recordElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordElement(tcjParser.RecordElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinition(tcjParser.DefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#interleaveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterleaveExpr(tcjParser.InterleaveExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#parallelExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParallelExpr(tcjParser.ParallelExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#paralDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParalDef(tcjParser.ParalDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#paralDef2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParalDef2(tcjParser.ParalDef2Context ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#internalChoiceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInternalChoiceExpr(tcjParser.InternalChoiceExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#externalChoiceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExternalChoiceExpr(tcjParser.ExternalChoiceExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#interruptExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterruptExpr(tcjParser.InterruptExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#hidingExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHidingExpr(tcjParser.HidingExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#sequentialExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequentialExpr(tcjParser.SequentialExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#guardExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGuardExpr(tcjParser.GuardExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#timeoutExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeoutExpr(tcjParser.TimeoutExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#withinExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithinExpr(tcjParser.WithinExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#deadlineExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeadlineExpr(tcjParser.DeadlineExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#waituntilExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWaituntilExpr(tcjParser.WaituntilExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#channelExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChannelExpr(tcjParser.ChannelExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#eventExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventExpr(tcjParser.EventExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#caseExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseExpr(tcjParser.CaseExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#caseCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseCondition(tcjParser.CaseConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#ifExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExpr(tcjParser.IfExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#ifExprs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExprs(tcjParser.IfExprsContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#ifBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfBlock(tcjParser.IfBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#atomicExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomicExpr(tcjParser.AtomicExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(tcjParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#eventM}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventM(tcjParser.EventMContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#eventList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventList(tcjParser.EventListContext ctx);
	/**
	 * Visit a parse tree produced by {@link tcjParser#eventName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventName(tcjParser.EventNameContext ctx);
}
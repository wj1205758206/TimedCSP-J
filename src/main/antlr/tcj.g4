 grammar tcj;
specification

            : (specBody)*

            ;



specBody
                 : library
                 | letDefintion
                 | definition
                 | assertion
                 | alphabet
                 | define
                 | channel
                 ;



library
                  : '#' 'import' STRING ';' //import the library by full dll path or DLL name under the Lib folder

            | '#' 'include' STRING ';' //include the other models by full path or file name if under the same folder of current model

            ;



channel

             : 'channel' ID ('[' additiveExpression ']')? additiveExpression ';'

             ;



assertion

            : '#' 'assert' definitionRef

            (

                        ( '|=' ( '(' | ')' | '[]' | '<>' | (ID | XNotAllowed) | STRING | '!' | '?' | '&&' | '||' | '->' | '<->' | '/\\' | '\\/' | '.' | INT )+ ) // 'X' is not allowed in LTL

                                    |  'deadlockfree'

                                    |  'deterministic'

                                    |  'nonterminating'

                                    |  'reaches' ID withClause?

                                    |  'refines' definitionRef

                                    |  'refines' '<F>' definitionRef

                                    |  'refines' '<FD>' definitionRef

                             |  'refines' '<T>' definitionRef   //timed refinement

            )

            ';'

            ;
fragment XNotAllowed
    : ('a'..'w'|'y'|'z'|'A'..'W'|'Y'|'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;



withClause
                    : 'with' ('min' | 'max') '(' expression ')'
                    ;



definitionRef

              : ID ('(' (argumentExpression(',' argumentExpression)*)? ')')?

              ;




alphabet

             : '#' 'alphabet' ID '{' eventList (',' eventList )* '}' ';'

             ;






define

             : '#' 'define' ID  '-'? INT ';'

             | '#' 'define' ID  ('true' ';'

                                         | 'false' ';')

             | 'enum' '{' a=ID(',' b=ID)* '}'  ';'

             | '#' 'define' ID dparameter? dstatement ';'

             ;


      dparameter
                  :  '(' ID (',' ID )* ')'

            ;


      dstatement
                 : block
                 | expression
                 ;



block

           : '{' (s=statement)* (e=expression)? '}' //At least a statement or expression has to be specified, i.e. s and e cannot be both null.

           ;



statement

          : block

          | localVariableDeclaration

          | ifExpression

          | whileExpression

          | expression ';'

          | ';'

          ;



//local variable that can be used in the block

localVariableDeclaration
                : 'var' ID ('=' expression)? ';'

          | 'var' ID '=' recordExpression ';'

          | 'var' ID ('[' expression ']')+ ('=' recordExpression)? ';'

          ;






expression

          : conditionalOrExpression ('=' expression)?

          ;



conditionalOrExpression

          : '||' indexedExpression

          | conditionalAndExpression ( '||' conditionalAndExpression )*

          ;





conditionalAndExpression

          : '&&' indexedExpression

          | conditionalXorExpression ( '&&' conditionalXorExpression)*

          ;



conditionalXorExpression

          : 'xor' indexedExpression

          | bitwiseLogicExpression ( 'xor' bitwiseLogicExpression)*

          ;



indexedExpression
                : (paralDef (';' paralDef )*) '@' expression
                ;



bitwiseLogicExpression
                : equalityExpression ( ( '&' | '|' | '^' ) equalityExpression)*

          ;



equalityExpression

            : relationalExpression ( ('=='|'!=') relationalExpression)*

            ;



relationalExpression

            : additiveExpression ( ('<' | '>' | '<=' | '>=') additiveExpression)*

            ;




additiveExpression

            : multiplicativeExpression ( ('+' | '-') multiplicativeExpression)*

            ;


multiplicativeExpression

            : unaryExpression ( ('*' | '/' | '%' ) unaryExpression)*

            ;



unaryExpression

            : '+' unaryExpression

            | '-' unaryExpression

            | '!' unaryExpressionNotPlusMinus

            | unaryExpressionNotPlusMinus '++' //Note: this is a syntax suger for unaryExpressionNotPlusMinus = unaryExpressionNotPlusMinus +1

          | unaryExpressionNotPlusMinus '--' //Note: this is a syntax suger for unaryExpressionNotPlusMinus = unaryExpressionNotPlusMinus - 1

            | unaryExpressionNotPlusMinus

            ;



arrayExpression

           : ID ('[' conditionalOrExpression ']')+

           ;






unaryExpressionNotPlusMinus

          : INT
                | 'true'
                | 'false'
                | 'call' '(' ID (',' argumentExpression)* ')'

          | 'new' ID '(' (argumentExpression (',' argumentExpression)*)? ')'

          |var=ID methods_fields_call

          | a1=arrayExpression methods_fields_call

          | arrayExpression

          | '(' conditionalOrExpression ')'

          | ID

          ;




methods_fields_call
               : '.' method=ID ('(' (argumentExpression (',' argumentExpression)* )? ')' )

         | '$' method=ID

         ;



letDefintion

        : ('var'|'hvar') ('<' userType=ID '>')? name=ID variableRange? ('=' (expression|'*') )? ';' //user defined datatype is supported using <type>

        | ('var'|'hvar') ID variableRange? '=' recordExpression ';'

       | ('var'|'hvar') ID ('[' expression ']')+ variableRange? ('=' (recordExpression|'*') )? ';' //multi-dimensional array is supported

        ;



variableRange
              : ':' '{' (additiveExpression)? '..' (additiveExpression)? '}'
              ;



argumentExpression
             : conditionalOrExpression

       | recordExpression
             ;




//if definition

ifExpression
    :  'if' '(' expression ')' statement ('else' statement)?
    ;

whileExpression
    : 'while' '(' expression ')' statement
    ;

recordExpression
    : '[' recordElement (',' recordElement)* ']'
    ;

recordElement
    : e1=expression ('(' e2=expression ')')?
    | e1=expression '..' e2=expression
    ;

definition
    : ID ('(' (ID (',' ID )*)? ')')? '=' interleaveExpr ';'
    ;

interleaveExpr
    : parallelExpr ('|||' parallelExpr)*
    | '|||' (paralDef (';' paralDef )*) '@' interleaveExpr
    ;



parallelExpr

            : internalChoiceExpr ('||' internalChoiceExpr)*

            | '||' (paralDef (';' paralDef )*) '@' interleaveExpr

            ;



paralDef

            : ID ':' '{' additiveExpression (',' additiveExpression)*  '}'

            | ID ':' '{' additiveExpression '..' additiveExpression  '}'

            ;



paralDef2

            : '{' '..' '}'

            | '{' additiveExpression '}'

            ;



internalChoiceExpr

            : externalChoiceExpr ('<>' externalChoiceExpr)*

            | '<>' (paralDef (';' paralDef )*) '@' interleaveExpr

            ;



externalChoiceExpr

            : interruptExpr ('[]' interruptExpr)*

            | '[]' (paralDef (';' paralDef )*) '@' interleaveExpr

            ;


interruptExpr

            : hidingExpr ('interrupt' ('['expression']')? hidingExpr)*  //if there is a time expression, it is a timed interrupt

            ;



hidingExpr

            : sequentialExpr ('\\' '{' eventName  (',' eventName )* '}')?

            ;



sequentialExpr

            : guardExpr (';' guardExpr)*

            ;


guardExpr

            : timeoutExpr

            | '[' conditionalOrExpression ']' timeoutExpr

            ;



timeoutExpr
          : withinExpr ('timeout' '['  expression ']'  withinExpr)*
          ;


withinExpr
          : deadlineExpr
          |  deadlineExpr 'within' '[' expression (',' expression)? ']'
          ;

deadlineExpr
          : waituntilExpr 'deadline' '[' expression ']'

     | waituntilExpr
          ;



waituntilExpr
          : channelExpr 'waituntil' '[' expression ']'

     | channelExpr
          ;






channelExpr

             : ID '!' expression ('.' expression)*  ('->'|'->>') eventExpr

             | ID '?' ('[' conditionalOrExpression ']')? expression ('.' expression)*  ('->'|'->>') eventExpr  //here expression is either a single variable or expression that has no global variables. Optional conditionalOrExpression is the guard condition that stop the channel input event if the condition is false.

             | eventExpr

             ;



eventExpr

             : eventM (block)? ('->'|'->>') eventExpr //->> means urgent event, which takes no time.

             | block ('->'|'->>') eventExpr //un-labeled program, which is same as: tau block '->' eventExpr

             | caseExpr

             ;


caseExpr: 'case'

              '{'

                        caseCondition+

                        ('default' ':' elsec=interleaveExpr)?

               '}'

            | ifExpr

            ;



caseCondition

            : (conditionalOrExpression ':' interleaveExpr)

            ;


ifExpr  :    atomicExpr

            | ifExprs

            ;



ifExprs

            : 'if' '(' conditionalOrExpression ')' '{' interleaveExpr  '}' ('else' ifBlock )?

            | 'ifa' '(' conditionalOrExpression ')' '{' interleaveExpr  '}' ('else' ifBlock )?

            | 'ifb' '(' conditionalOrExpression ')' '{' interleaveExpr  '}'

            ;



ifBlock

            : ifExprs

            | '{' interleaveExpr '}'

            ;



atomicExpr

            : atom

            | 'atomic' '{' interleaveExpr  '}'

            ;




atom   :   ID  ('(' (expression (',' expression )*)?  ')')?

            |   'Skip' ('(' ')')?

            |   'Stop' ('(' ')')?

            |   'Wait' '[' expression (',' expression)? ']'

            |   '(' interleaveExpr ')'

            ;







eventM

            :  eventName



              | 'tau' //invisible tau event, which takes no time.
            ;




eventList

            : eventName
                  | (paralDef (';' paralDef )*) '@' eventName

            ;






eventName

            : ID ( '.' additiveExpression)*

            ;





ID        : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*

            ;



//string allows user to input the channel input/output as propersitions in the LTL

STRING

:  '"' (~('\\'|'"') )* '"'

            ;



WS      :  ('  ' | '\t' | '\n' | '\r' | '\f');



INT :  ('0'..'9')+ ;



COMMENT : '/*' .*? '*/' -> channel(HIDDEN);





LINE_COMMENT

            : '//' ~('\n'|'\r')* '\r'? '\n'

            ;


package common.classes.moduleinterface;

import common.ParsingException;

public class Declaration {
    public Declaration(DeclarationType type, ParsingException token)
    {
        this.declarationType = type;
        this.declarationToken = token;
    }


    public DeclarationType declarationType;


    public ParsingException declarationToken;
}

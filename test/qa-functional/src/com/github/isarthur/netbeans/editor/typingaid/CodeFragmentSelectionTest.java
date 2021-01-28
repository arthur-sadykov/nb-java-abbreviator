/*
 * Copyright 2021 Arthur Sadykov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.isarthur.netbeans.editor.typingaid;

import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;
import java.io.IOException;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.ProjectRootNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;

/**
 *
 * @author: Arthur Sadykov
 */
public class CodeFragmentSelectionTest extends JellyTestCase {

    private static final String TEST_PROJECT_NAME = "TestProject";
    private boolean keyword;
    private boolean literal;
    private boolean modifier;
    private boolean primitiveType;
    private boolean externalType;
    private boolean internalType;
    private boolean globalType;
    private boolean resourceVariable;
    private boolean exceptionParameter;
    private boolean enumConstant;
    private boolean parameter;
    private boolean field;
    private boolean localVariable;
    private boolean localMethodInvocation;
    private boolean chainedMethodInvocation;
    private boolean chainedFieldAccess;
    private boolean chainedEnumConstantAccess;
    private boolean staticMethodInvocation;
    private boolean staticFieldAccess;
    private boolean methodInvocation;
    private EditorOperator editorOperator;

    public CodeFragmentSelectionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return createModuleTest(CodeFragmentSelectionTest.class);
    }

    @Override
    public void setUp() throws IOException {
        storeCodeCompletionConfiguration();
        resetCodeCompletionConfiguration();
        openDataProjects(TEST_PROJECT_NAME);
        ProjectsTabOperator projectsTabOperator = ProjectsTabOperator.invoke();
        ProjectRootNode projectRootNode = projectsTabOperator.getProjectRootNode(TEST_PROJECT_NAME);
        Node node = new Node(projectRootNode, "Source Packages|test|Test");
        OpenAction openAction = new OpenAction();
        openAction.performAPI(node);
        editorOperator = new EditorOperator("Test");
    }

    private void storeCodeCompletionConfiguration() {
        staticMethodInvocation = Preferences.getStaticMethodInvocationFlag();
        staticFieldAccess = Preferences.getStaticFieldAccessFlag();
        methodInvocation = Preferences.getMethodInvocationFlag();
        chainedMethodInvocation = Preferences.getChainedMethodInvocationFlag();
        chainedFieldAccess = Preferences.getChainedFieldAccessFlag();
        chainedEnumConstantAccess = Preferences.getChainedEnumConstantAccessFlag();
        localMethodInvocation = Preferences.getLocalMethodInvocationFlag();
        localVariable = Preferences.getLocalVariableFlag();
        field = Preferences.getFieldFlag();
        parameter = Preferences.getParameterFlag();
        enumConstant = Preferences.getEnumConstantFlag();
        exceptionParameter = Preferences.getExceptionParameterFlag();
        resourceVariable = Preferences.getResourceVariableFlag();
        internalType = Preferences.getInternalTypeFlag();
        externalType = Preferences.getExternalTypeFlag();
        globalType = Preferences.getGlobalTypeFlag();
        keyword = Preferences.getKeywordFlag();
        literal = Preferences.getLiteralFlag();
        modifier = Preferences.getModifierFlag();
        primitiveType = Preferences.getPrimitiveTypeFlag();
    }

    private void resetCodeCompletionConfiguration() {
        Preferences.setStaticMethodInvocationFlag(false);
        Preferences.setStaticFieldAccessFlag(false);
        Preferences.setMethodInvocationFlag(false);
        Preferences.setChainedMethodInvocationFlag(false);
        Preferences.setChainedFieldAccessFlag(false);
        Preferences.setChainedEnumConstantAccessFlag(false);
        Preferences.setLocalMethodInvocationFlag(false);
        Preferences.setLocalVariableFlag(false);
        Preferences.setFieldFlag(false);
        Preferences.setParameterFlag(false);
        Preferences.setEnumConstantFlag(false);
        Preferences.setExceptionParameterFlag(false);
        Preferences.setResourceVariableFlag(false);
        Preferences.setInternalTypeFlag(false);
        Preferences.setExternalTypeFlag(false);
        Preferences.setGlobalTypeFlag(false);
        Preferences.setKeywordFlag(false);
        Preferences.setLiteralFlag(false);
        Preferences.setModifierFlag(false);
        Preferences.setPrimitiveTypeFlag(false);
    }

    public void testShouldSelectConditionInAssertStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(700, new char[]{'a'}, "true");
    }

    public void testShouldSelectConditionInDoWhileStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(700, new char[]{'d'}, "true");
    }

    public void testShouldSelectConditionInIfStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(700, new char[]{'i'}, "true");
    }

    public void testShouldSelectConditionInElseIfStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(719, new char[]{'e'}, "true");
    }

    public void testShouldSelectConditionInWhileStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(700, new char[]{'w'}, "true");
    }

    public void testShouldSelectTypeInThrowStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsertExpectingClashes(700, new char[]{'t'}, "throw", "IllegalArgumentException");
    }

    public void testShouldSelectExceptionParameterNameInTryStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsertExpectingClashes(700, new char[]{'t'}, "try", "e");
    }

    public void testShouldSelectNameOfMethodHavingVoidReturnType() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(933, new char[]{'v'}, "method");
    }

    public void testShouldSelectNameOfFieldThatIsOfPrimitiveType() {
        Preferences.setPrimitiveTypeFlag(true);
        doAbbreviationInsert(700, new char[]{'d'}, "d");
    }

    public void testShouldSelectNameOfMethodHavingPrimitiveReturnType() {
        Preferences.setPrimitiveTypeFlag(true);
        doAbbreviationInsert(933, new char[]{'d'}, "method");
    }

    public void testShouldSelectNameOfMethodParameterThatIsOfPrimitiveType() {
        Preferences.setPrimitiveTypeFlag(true);
        doAbbreviationInsert(688, new char[]{'d'}, "d");
    }

    public void testShouldSelectNameOfFieldThatIsOfReferenceType() {
        Preferences.setGlobalTypeFlag(true);
        doAbbreviationInsert(700, new char[]{'b', 'm', 'e'}, "bootstrapMethodError");
    }

    public void testShouldSelectNameOfMethodHavingReferenceReturnType() {
        Preferences.setGlobalTypeFlag(true);
        doAbbreviationInsert(933, new char[]{'b', 'm', 'e'}, "method");
    }

    public void testShouldSelectNameOfMethodParameterThatIsOfReferenceType() {
        Preferences.setGlobalTypeFlag(true);
        doAbbreviationInsert(688, new char[]{'b', 'm', 'e'}, "bootstrapMethodError");
    }

    public void testShouldSelectClassName() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(933, new char[]{'c'}, "Class");
    }

    public void testShouldSelectEnumName() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(933, new char[]{'e'}, "Enum");
    }

    public void testShouldSelectInterfaceName() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(933, new char[]{'i'}, "Interface");
    }

    public void testShouldSelectExceptionParameterInCatchClause() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(870, new char[]{'c'}, "e");
    }

    public void testShouldSelectNameInMethodInvocationHavingPrimitiveReturnType() {
        Preferences.setLocalVariableFlag(true);
        Preferences.setMethodInvocationFlag(true);
        doAbbreviationInsert(1022, new char[]{'n', '.', 'e', 'w'}, "b");
    }

    public void testShouldSelectNameInMethodInvocationHavingReferenceReturnType() {
        Preferences.setLocalVariableFlag(true);
        Preferences.setMethodInvocationFlag(true);
        doAbbreviationInsert(1022, new char[]{'n', '.', 'c', 'p'}, "intStream");
    }

    public void testShouldSelectFirstParameterNameInMethodInvocationHavingVoidReturnType() {
        Preferences.setLocalVariableFlag(true);
        Preferences.setMethodInvocationFlag(true);
        doAbbreviationInsert(1169, new char[]{'s', 'b', '.', 's', 'c', 'a'}, "index");
    }

    public void testShouldSelectLiteralInConditionOfForLoop() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(1169, new char[]{'f'}, "10");
    }

    public void testShouldSelectExpressionInReturnStatement() {
        Preferences.setKeywordFlag(true);
        Preferences.setLocalVariableFlag(true);
        doAbbreviationInsert(1230, new char[]{'r'}, "size");
    }

    public void testShouldSelectExpressionInSwitchStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsertExpectingClashes(1269, new char[]{'s'}, "switch", null);
        assertEquals(1277, editorOperator.txtEditorPane().getCaretPosition());
    }

    public void testShouldSelectExpressionInCaseClause() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(1354, new char[]{'c'}, null);
        assertEquals(1359, editorOperator.txtEditorPane().getCaretPosition());
    }

    public void testShouldSelectValueInAssignmentOperator() {
        Preferences.setLocalVariableFlag(true);
        doAbbreviationInsert(1438, new char[]{'l', 'v'}, "\"\"");
    }

    public void testShouldSelectFirstArgumentOfMethodInvocationInConditionalExpression() {
        Preferences.setLocalVariableFlag(true);
        Preferences.setMethodInvocationFlag(true);
        doAbbreviationInsert(1576, new char[]{'b', '.', 'd', 'c', 'a'}, "index");
    }

    public void testShouldSelectTypeInInstanceofOperator() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(1683, new char[]{'i'}, null);
        assertEquals(1695, editorOperator.txtEditorPane().getCaretPosition());
    }

    public void testShouldSelectExpressionInSynchronizedStatement() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsertExpectingClashes(1746, new char[]{'s'}, "synchronized", null);
        assertEquals(1760, editorOperator.txtEditorPane().getCaretPosition());
    }

    public void testShouldSelectTypeInExtendsClauseOfClassDeclaration() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(670, new char[]{'e'}, "Object");
    }

    public void testShouldSelectTypeInExtendsClauseOfInterfaceDeclaration() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(1778, new char[]{'e'}, "Cloneable");
    }

    public void testShouldSelectTypeInImplementsClauseOfClassDeclaration() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(670, new char[]{'i'}, "Cloneable");
    }

    public void testShouldSelectTypeInImplementsClauseOfEnumDeclaration() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(1814, new char[]{'i'}, "Cloneable");
    }

    public void testShouldSelectTypeInThrowsClause() {
        Preferences.setKeywordFlag(true);
        doAbbreviationInsert(690, new char[]{'t'}, "IllegalArgumentException");
    }

    public void testShouldSelectFirstArgumentInThrowStatement() {
        Preferences.setInternalTypeFlag(true);
        doAbbreviationInsert(2040, new char[]{'t', 'e'}, "null");
    }

    private void doAbbreviationInsert(int caretPosition, char[] chars, String expected) {
        typeAbbreviation(caretPosition, chars);
        new EventTool().waitNoEvent(2000);
        assertEquals(expected, editorOperator.txtEditorPane().getSelectedText());
    }

    private void doAbbreviationInsertExpectingClashes(int caretPosition, char[] chars, String item, String expected) {
        typeAbbreviation(caretPosition, chars);
        selectItemInPopupWindow(item);
        new EventTool().waitNoEvent(2000);
        assertEquals(expected, editorOperator.txtEditorPane().getSelectedText());
    }

    private void typeAbbreviation(int caretPosition, char[] chars) {
        editorOperator.setCaretPosition(caretPosition);
        for (char c : chars) {
            editorOperator.typeKey(c);
        }
        editorOperator.typeKey(' ');
    }

    private void selectItemInPopupWindow(String item) {
        MainWindowOperator windowOperator = new MainWindowOperator();
        JDialogOperator dialogOperator = new JDialogOperator(windowOperator);
        JListOperator listOperator = new JListOperator(dialogOperator);
        listOperator.selectItem(item);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        revertCodeCompletionConfiguration();
        editorOperator.closeDiscard();
    }

    private void revertCodeCompletionConfiguration() {
        Preferences.setMethodInvocationFlag(methodInvocation);
        Preferences.setStaticMethodInvocationFlag(staticMethodInvocation);
        Preferences.setChainedMethodInvocationFlag(chainedMethodInvocation);
        Preferences.setChainedFieldAccessFlag(chainedFieldAccess);
        Preferences.setChainedEnumConstantAccessFlag(chainedEnumConstantAccess);
        Preferences.setLocalMethodInvocationFlag(localMethodInvocation);
        Preferences.setStaticFieldAccessFlag(staticFieldAccess);
        Preferences.setLocalVariableFlag(localVariable);
        Preferences.setFieldFlag(field);
        Preferences.setParameterFlag(parameter);
        Preferences.setEnumConstantFlag(enumConstant);
        Preferences.setExceptionParameterFlag(exceptionParameter);
        Preferences.setResourceVariableFlag(resourceVariable);
        Preferences.setInternalTypeFlag(internalType);
        Preferences.setExternalTypeFlag(externalType);
        Preferences.setGlobalTypeFlag(globalType);
        Preferences.setKeywordFlag(keyword);
        Preferences.setLiteralFlag(literal);
        Preferences.setModifierFlag(modifier);
        Preferences.setPrimitiveTypeFlag(primitiveType);
    }
}

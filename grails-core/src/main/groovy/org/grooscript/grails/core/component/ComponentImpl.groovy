package org.grooscript.grails.core.component

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.builder.HtmlBuilder

import java.lang.reflect.Modifier

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class ComponentImpl implements ASTTransformation {

    private static final String RENDER_METHOD = 'render'
    private static final String STYLE = 'style'

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            return
        }

        ClassNode classNode = nodes[1] as ClassNode

        checkMethodsToAddRenderCall(classNode)

        classNode.addProperty('shadowRoot', Modifier.PUBLIC , ClassHelper.OBJECT_TYPE, null, null, null)
        classNode.addProperty('cId', Modifier.PUBLIC , ClassHelper.Number_TYPE, null, null, null)

        HtmlMapVisitor htmlMapVisitor = new HtmlMapVisitor(classNode: classNode)
        classNode.visitContents(htmlMapVisitor)

        manageRenderMethod(classNode)
    }

    private static void checkMethodsToAddRenderCall(ClassNode classNode) {
        PropertyNode renderAfter = classNode.properties.find { it.name == 'renderAfter' && it.static }
        if (!renderAfter)
            return

        switch (renderAfter.initialExpression) {
            case ConstantExpression:
                addRenderCallToMethod(renderAfter.initialExpression as ConstantExpression, classNode)
                break
            case ListExpression:
                ListExpression list = renderAfter.initialExpression as ListExpression
                list.expressions.each { expression ->
                    if (expression instanceof ConstantExpression)
                        addRenderCallToMethod(expression, classNode)
                }
                break
            default:
                throw new GroovyRuntimeException("Unexpected expression type " + renderAfter.initialExpression.type)
                break
        }
    }

    private static void addRenderCallToMethod(ConstantExpression constantExpression, ClassNode classNode) {
        if (constantExpression.value != RENDER_METHOD) {
            MethodNode methodNode = classNode.methods.find { it.name == constantExpression.value }
            if (methodNode?.code instanceof BlockStatement) {
                BlockStatement block = methodNode.code as BlockStatement
                block.addStatement(stmt(callThisX(RENDER_METHOD)))
            }
        }
    }

    private static manageRenderMethod(ClassNode classNode) {
        MethodNode renderMethod = classNode.methods.find { it.name == RENDER_METHOD}
        if (!renderMethod) {
            throw new GroovyRuntimeException("You have to define a ${RENDER_METHOD} method.")
        } else {
            BlockStatement actualCode = renderMethod.code as BlockStatement
            VariableScope variableScope = actualCode.getVariableScope()

            ClosureExpression closure = closureX(actualCode)
            closure.setVariableScope(variableScope)

            renderMethod.setCode(
                block(
                    variableScope, stmt(
                        assignX(
                            propX(
                                propX(
                                    varX('this', ClassHelper.OBJECT_TYPE), 'shadowRoot'
                                ),
                                'innerHTML'
                            ),
                            htmlExpression(closure, classNode)
                        )
                    )
                )
            )
        }
    }

    private static Expression htmlExpression(ClosureExpression closure, ClassNode classNode) {
        if (classNode.properties.any { it.name == STYLE && it.static })
            buildStyleAndHtmlExpression(closure)
        else
            buildHtmlExpression(closure)
    }

    private static Expression buildStyleAndHtmlExpression(ClosureExpression closure) {
        plusX(
            new GStringExpression(null, [constX('<style>'), constX('</style>')], [varX(STYLE)]),
            buildHtmlExpression(closure)
        )
    }

    private static Expression buildHtmlExpression(ClosureExpression closure) {
        callX(classX(ClassHelper.makeCached(HtmlBuilder)), 'build', args([closure]))
    }

}
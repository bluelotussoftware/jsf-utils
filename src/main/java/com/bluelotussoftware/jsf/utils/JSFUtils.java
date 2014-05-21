/*
 * Copyright 2012-2014 Blue Lotus Software, LLC.
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
/*
 * $Id$
 */
package com.bluelotussoftware.jsf.utils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;
import javax.servlet.http.HttpServletRequest;

/**
 * A collection of utility methods that handle repetitive boilerplate code.
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.6.3
 */
public class JSFUtils implements Serializable {

    private static final long serialVersionUID = 4005663315445526130L;

    /**
     * Creates a {@link ValueExpression} that wraps an object instance. This
     * method can be used to pass any object as a {@link ValueExpression}. The
     * wrapper {@link ValueExpression} is read only, and returns the wrapped
     * object via its {@code getValue()} method, optionally coerced.
     *
     * @param expression The expression to be parsed.
     * @param expectedType The type the result of the expression will be coerced
     * to after evaluation.
     * @return The parsed expression.
     * @see #createValueExpression(java.lang.String)
     */
    public static ValueExpression createValueExpression(final String expression, Class<?> expectedType) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), expression, expectedType);
    }

    /**
     * Creates a {@link ValueExpression} that wraps an object instance. This
     * method can be used to pass any object as a {@link ValueExpression}. The
     * wrapper {@link ValueExpression} is read only, and returns the wrapped
     * object via its {@code getValue()} method, optionally coerced.
     *
     * @param expression The expression to be parsed.
     * @return The parsed expression.
     * @since 1.4
     * @see #createValueExpression(java.lang.String, java.lang.Class)
     */
    public static ValueExpression createValueExpression(final String expression) {
        return createValueExpression(expression, Object.class);
    }

    /**
     * <p>
     * This convenience method sets a {@link ValueExpression} to a specific
     * value.</p>
     * <p>
     * An example, {@literal #{currentDate}} could be mapped to the using
     * {@literal JSFUtils.setValueExpressionToValue(new Date(System.currentTimeMillis), "#{currentDate}")}.
     * This is useful when setting {@literal <f:param />} values.</p>
     *
     * @param value The {@code Object} to set as the {@link ValueExpression}
     * target.
     * @param expression The expression to be parsed.
     * @since 1.4
     * @see #createValueExpression(java.lang.String, java.lang.Class)
     */
    public static void setValueExpressionToValue(final Object value, final String expression) {
        ValueExpression ve = createValueExpression(expression);
        ve.setValue(FacesContext.getCurrentInstance().getELContext(), value);
    }

    /**
     * <p>
     * This convenience method is used to map a variable to a
     * {@link ValueExpression}.</p>
     * <p>
     * An example usage could be using the value from a list and mapping it to a
     * variable, e.g.,
     * {@literal JSFUtils.mapVariableToValueExpression("Phone", "#{directory.listings['Office']}")}.
     * </p>
     *
     * @param variable The variable name to map.
     * @param expression The expression to be mapped to the variable.
     * @since 1.4
     *
     */
    public static void mapVariableToValueExpression(final String variable, final String expression) {
        ValueExpression ve = createValueExpression(expression);
        FacesContext.getCurrentInstance().getELContext().getVariableMapper().setVariable(variable, ve);
    }

    /**
     * This is a convenience method that parses an expression into a
     * {@link MethodExpression} for later evaluation. Use this method for
     * expressions that refer to methods. If the expression is a {@code String}
     * literal, a {@link MethodExpression} is created, which when invoked,
     * returns the {@code String} literal, coerced to expectedReturnType. An
     * {@link ELException} is thrown if expectedReturnType is {@code void} or if
     * the coercion of the {@code String} literal to the expectedReturnType
     * yields an error. This method should perform syntactic validation of the
     * expression. If in doing so it detects errors, it should raise an
     * {@link ELException}.
     *
     * @param methodExpression The expression to parse.
     * @param expectedReturnType The expected return type for the method to be
     * found. After evaluating the expression, the {@link MethodExpression} must
     * check that the return type of the actual method matches this type.
     * Passing in a value of {@code null} indicates the caller does not care
     * what the return type is, and the check is disabled.
     * @param expectedParamTypes The expected parameter types for the method to
     * be found. Must be an array with no elements if there are no parameters
     * expected. It is illegal to pass {@code null}, unless the method is
     * specified with arguments in the EL expression, in which case these
     * arguments are used for method selection, and this parameter is ignored.
     * @return The parsed expression.
     */
    public static MethodExpression createMethodExpression(final String methodExpression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getExpressionFactory()
                .createMethodExpression(context.getELContext(), methodExpression, expectedReturnType, expectedParamTypes);
    }

    /**
     * This is a convenience method that produces an {@link MethodExpression} to
     * handle an {@link ActionEvent}.
     *
     * @param expression The expression to be parsed.
     * @return The parsed expression.
     * @see JSFUtils#createMethodExpression(java.lang.String, java.lang.Class,
     * java.lang.Class<?>[])
     * @see JSFUtils#createActionEventMethodExpression(java.lang.String)
     * @deprecated
     */
    @Deprecated
    public static MethodExpression createActionEventListenerMethodExpression(final String expression) {
        return createActionEventMethodExpression(expression);
    }

    /**
     * This is a convenience method that produces an {@link MethodExpression} to
     * handle an {@link ActionEvent}.
     *
     * @param expression The expression to be parsed.
     * @return The parsed expression.
     * @since 1.4
     * @see #createMethodExpression(java.lang.String, java.lang.Class,
     * java.lang.Class<?>[])
     */
    public static MethodExpression createActionEventMethodExpression(final String expression) {
        Class<?>[] expectedParamTypes = new Class<?>[1];
        expectedParamTypes[0] = ActionEvent.class;
        return createMethodExpression(expression, Void.TYPE, expectedParamTypes);
    }

    /**
     * This convenience method creates a {@link MethodExpression} for use as an
     * action.
     *
     * This is used to set the {@link MethodExpression} pointing at the
     * application action to be invoked, on a {@link UIComponent} activated by
     * the user, during the Apply Request Values or Invoke Application phase of
     * the request processing lifecycle, depending on the value of the immediate
     * property.
     *
     * Any method referenced by such an expression must be public, with a return
     * type of {@code String}, and accept no parameters.
     *
     * @param expression The expression to be parsed.
     * @return An expression that has a {@code String} return value and no
     * parameters.
     * @since 1.4
     * @see #createMethodExpression(java.lang.String, java.lang.Class,
     * java.lang.Class<?>[])
     */
    public static MethodExpression createActionExpression(final String expression) {
        return createMethodExpression(expression, String.class, new Class<?>[]{});
    }

    /**
     * This is a convenience method that produces a {@link ActionListener} for a
     * {@link MethodExpression}
     *
     * @param methodExpression The expression to parse.
     * @param expectedReturnType The expected return type for the method to be
     * found. After evaluating the expression, the {@link MethodExpression} must
     * check that the return type of the actual method matches this type.
     * Passing in a value of {@code null} indicates the caller does not care
     * what the return type is, and the check is disabled.
     * @param expectedParamTypes The expected parameter types for the method to
     * be found. Must be an array with no elements if there are no parameters
     * expected. It is illegal to pass {@code null}, unless the method is
     * specified with arguments in the EL expression, in which case these
     * arguments are used for method selection, and this parameter is ignored.
     * @return an {@link ActionListener} that wraps a {@link
     * MethodExpression}. When it receives a {@link ActionEvent}, it executes a
     * method on an object identified by the {@link
     * MethodExpression}.
     * @since 1.4
     * @see #createMethodExpression(java.lang.String, java.lang.Class,
     * java.lang.Class<?>[])
     */
    public static MethodExpressionActionListener createMethodExpressionActionListener(final String methodExpression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        return new MethodExpressionActionListener(createMethodExpression(methodExpression, expectedReturnType, expectedParamTypes));
    }

    /**
     * Return a reference to the bean from one of the following: {@link javax.faces.context.ExternalContext#getApplicationMap()}, {@link javax.faces.context.ExternalContext#getSessionMap()},
     * {@link javax.faces.component.UIViewRoot#getViewMap()}, {@link javax.faces.context.ExternalContext#getRequestMap()},
     * and {@link javax.faces.context.ExternalContext#getFlash()} objects.
     *
     * @param <T> type of class.
     * @param beanName name of the class to search for in {@link javax.faces.context.ExternalContext#getApplicationMap()}, {@link javax.faces.context.ExternalContext#getSessionMap()},
     * {@link javax.faces.component.UIViewRoot#getViewMap()}, {@link javax.faces.context.ExternalContext#getRequestMap()},
     * and {@link javax.faces.context.ExternalContext#getFlash()} objects.
     * @return referenced bean, or {@literal null} if it can not be found.
     * @since 1.4
     * @see #getBean(java.lang.String)
     * @see #getBean(java.lang.String, java.lang.Class)
     */
    public static <T> T getTypedBean(String beanName) {
        return (T) getBean(beanName);
    }

    /**
     * Return a reference to the bean from one of the following: {@link javax.faces.context.ExternalContext#getApplicationMap()}, {@link javax.faces.context.ExternalContext#getSessionMap()},
     * {@link javax.faces.component.UIViewRoot#getViewMap()}, {@link javax.faces.context.ExternalContext#getRequestMap()},
     * and {@link javax.faces.context.ExternalContext#getFlash()} objects.
     *
     * @param beanName name of the class to search for in {@link javax.faces.context.ExternalContext#getApplicationMap()}, {@link javax.faces.context.ExternalContext#getSessionMap()},
     * {@link javax.faces.component.UIViewRoot#getViewMap()}, {@link javax.faces.context.ExternalContext#getRequestMap()},
     * and {@link javax.faces.context.ExternalContext#getFlash()} objects.
     * @return referenced bean, or {@literal null} if it can not be found.
     * @since 1.4
     * @see #getTypedBean(java.lang.String)
     * @see #getBean(java.lang.String, java.lang.Class)
     */
    public static Object getBean(String beanName) {
        Object obj;
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();

        obj = ectx.getApplicationMap().get(beanName); // Check Application Scope

        if (obj == null) {
            obj = ectx.getSessionMap().get(beanName); // Check Session Scope 
        }

        if (obj == null) {
            obj = fctx.getViewRoot().getViewMap().get(beanName); // Check View Scope
        }

        if (obj == null) {
            obj = ectx.getRequestMap().get(beanName); // Check Request Scope
        }

        if (obj == null) {
            obj = ectx.getFlash().get(beanName); // Check Flash Scope
        }

        return obj;
    }

    /**
     * Return a reference to the bean from the
     * {@link javax.faces.context.FacesContext#getCurrentInstance()}.
     *
     * @param <T> type of class.
     * @param beanName name of the class to search for in
     * {@link javax.faces.context.FacesContext}.
     * @param beanClass class type of bean to return.
     * @return referenced bean.
     * @since 1.4
     * @see #getBean(java.lang.String)
     * @see #getTypedBean(java.lang.String)
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        return (T) application.evaluateExpressionGet(context, String.format("#{%s}", beanName), beanClass);
    }

    /**
     * This method provides a convenient means of determining the JSF
     * Specification version.
     *
     * @return JSF Specification version, e.g. 2.1
     * @since 1.5
     */
    public static String getSpecificationVersion() {
        return FacesContext.getCurrentInstance().getClass().getPackage().getSpecificationVersion();
    }

    /**
     * This method provides a convenient means of determining the JSF
     * Implementation version.
     *
     * @return JSF Implementation version, e.g. 2.1.26
     * @since 1.5
     */
    public static String getImplementationVersion() {
        return FacesContext.getCurrentInstance().getClass().getPackage().getImplementationVersion();
    }

    /**
     * This method provides a convenient means of determining the JSF
     * Implementation Title.
     *
     * @return JSF implementation title, e.g. Mojarra.
     * @since 1.5
     */
    public static String getImplementationTitle() {
        return FacesContext.getCurrentInstance().getClass().getPackage().getImplementationTitle();
    }

    /**
     * This method sets the bean name provided to {@code null}.
     *
     * @param beanName The name of the bean to remove.
     * @since 1.6
     */
    public static void removeManagedBean(final String beanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getELContext().getELResolver().setValue(fc.getELContext(), null, beanName, null);
    }

    /**
     * Programmatic method to create an &lt;h:commandLink/&gt;.
     *
     * @param context The current request context.
     * @param expression The EL expression to be parsed and set. The expression
     * must comply with the rules for an action expression.
     * @param value The output value of the component.
     * @return A complete {@link HtmlCommandLink} component.
     * @since 1.1
     * @see #createActionExpression(java.lang.String)
     */
    public static HtmlCommandLink createHtmlCommandLink(final FacesContext context, final String expression, final String value) {
        Application application = context.getApplication();
        HtmlCommandLink htmlCommandLink = (HtmlCommandLink) application.createComponent(HtmlCommandLink.COMPONENT_TYPE);
        htmlCommandLink.setValue(value);
        htmlCommandLink.setActionExpression(createActionExpression(expression));
        return htmlCommandLink;
    }

    /**
     * Programmatic method to create an &lt;h:commandButton/&gt;.
     *
     * @param context The current request context.
     * @param expression The EL expression to be parsed and set. The expression
     * must comply with the rules for an action expression.
     * @param value The output value of the component.
     * @return A complete {@link HtmlCommandButton} component.
     * @since 1.2
     * @see #createActionExpression(java.lang.String)
     */
    public static HtmlCommandButton createHtmlCommandButton(final FacesContext context, final String expression, final String value) {
        Application application = context.getApplication();
        HtmlCommandButton htmlCommandButton = (HtmlCommandButton) application.createComponent(HtmlCommandButton.COMPONENT_TYPE);
        htmlCommandButton.setValue(value);
        htmlCommandButton.setActionExpression(createActionExpression(expression));
        return htmlCommandButton;
    }

    /**
     * <p>
     * Determines the Base URL, e.g.,
     * {@literal http://localhost:8080/myApplication} from the
     * {@link FacesContext}.</p>
     *
     * @param facesContext The {@link FacesContext} to examine.
     * @return the base {@link URL}.
     * @throws MalformedURLException if an exception occurs during parsing of
     * the {@link URL}.
     * @since 1.3
     */
    public static String getBaseURL(final FacesContext facesContext) throws MalformedURLException {
        return getBaseURL(facesContext.getExternalContext());
    }

    /**
     * <p>
     * Determines the Base URL, e.g.,
     * {@literal http://localhost:8080/myApplication} from the
     * {@link ExternalContext}.</p>
     *
     * @param externalContext The {@link ExternalContext} to examine.
     * @return the base {@link URL}.
     * @throws MalformedURLException if an exception occurs during parsing of
     * the {@link URL}.
     * @since 1.3
     */
    public static String getBaseURL(final ExternalContext externalContext) throws MalformedURLException {
        return getBaseURL((HttpServletRequest) externalContext.getRequest());
    }

    /**
     * <p>
     * Determines the Base URL, e.g.,
     * {@literal http://localhost:8080/myApplication} from the
     * {@link HttpServletRequest}.</p>
     *
     * @param request The {@link HttpServletRequest} to examine.
     * @return the base {@link URL}.
     * @throws MalformedURLException if an exception occurs during parsing of
     * the {@link URL}.
     * @see URL
     * @since 1.3
     */
    public static String getBaseURL(final HttpServletRequest request) throws MalformedURLException {
        return new URL(request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath()).toString();
    }
}

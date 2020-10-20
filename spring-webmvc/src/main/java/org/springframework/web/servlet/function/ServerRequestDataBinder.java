package org.springframework.web.servlet.function;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServerRequestDataBinder extends WebDataBinder {

	/**
	 * Create a new {@code ServerRequestDataBinder} instance, with default object name.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public ServerRequestDataBinder(@Nullable Object target) {
		super(target);
	}

	/**
	 * Create a new {@code ServerRequestDataBinder} instance.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public ServerRequestDataBinder(@Nullable Object target, String objectName) {
		super(target, objectName);
	}

	/**
	 * Bind the parameters of the given request to this binder's target,
	 * also binding multipart files in case of a multipart request.
	 * <p>This call can create field errors, representing basic binding
	 * errors like a required field (code "required"), or type mismatch
	 * between value and bean property (code "typeMismatch").
	 * <p>Multipart files are bound via their parameter name, just like normal
	 * HTTP parameters: i.e. "uploadedFile" to an "uploadedFile" bean property,
	 * invoking a "setUploadedFile" setter method.
	 * <p>The type of the target property for a multipart file can be Part, MultipartFile,
	 * byte[], or String. The latter two receive the contents of the uploaded file;
	 * all metadata like original file name, content type, etc are lost in those cases.
	 * @param request the request with parameters to bind (can be multipart)
	 * @see org.springframework.web.multipart.MultipartRequest
	 * @see org.springframework.web.multipart.MultipartFile
	 * @see javax.servlet.http.Part
	 * @see #bind(org.springframework.beans.PropertyValues)
	 */
	public void bind(ServerRequest request) {
		HttpServletRequest httpServletRequest = request.servletRequest();
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(httpServletRequest);
		MultipartRequest multipartRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartRequest.class);
		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}
		else if (StringUtils.startsWithIgnoreCase(httpServletRequest.getContentType(), "multipart/")) {
			if (httpServletRequest != null) {
				StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
			}
		}
		addBindValues(mpvs, request);
		doBind(mpvs);
	}

	/**
	 * Extension point that subclasses can use to add extra bind values for a
	 * request. Invoked before {@link #doBind(MutablePropertyValues)}.
	 * The default implementation is empty.
	 * @param mpvs the property values that will be used for data binding
	 * @param request the current request
	 */
	protected void addBindValues(MutablePropertyValues mpvs, ServerRequest request) {
	}

	/**
	 * Treats errors as fatal.
	 * <p>Use this method only if it's an error if the input isn't valid.
	 * This might be appropriate if all input is from dropdowns, for example.
	 * @throws ServletRequestBindingException subclass of ServletException on any binding problem
	 */
	public void closeNoCatch() throws ServletRequestBindingException {
		if (getBindingResult().hasErrors()) {
			throw new ServletRequestBindingException(
					"Errors binding onto object '" + getBindingResult().getObjectName() + "'",
					new BindException(getBindingResult()));
		}
	}
}

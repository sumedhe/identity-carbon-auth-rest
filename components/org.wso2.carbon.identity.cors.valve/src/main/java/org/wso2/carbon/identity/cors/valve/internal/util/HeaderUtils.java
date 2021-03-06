/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * NOTE: The code/logic in this class is copied from https://bitbucket.org/thetransactioncompany/cors-filter.
 * All credits goes to the original authors of the project https://bitbucket.org/thetransactioncompany/cors-filter.
 */

package org.wso2.carbon.identity.cors.valve.internal.util;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Header utilities.
 */
public class HeaderUtils {

    /**
     * Must match "token", 1 or more of any US-ASCII char except control
     * chars or specific "separators", see: http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
     * and http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2
     * Note use of regex character class subtraction and character class metacharacter rules.
     */
    private static final Pattern VALID = compile("^[\\x21-\\x7e&&[^]\\[}{()<>@,;:\\\\\"/?=]]+$");

    /**
     * Private constructor of HeaderUtils.
     */
    private HeaderUtils() {

    }

    /**
     * Serialises the items of a set into a string. Each item must have a meaningful {@code toString()} method.
     *
     * @param set The set to serialise. Must not be {@code null}.
     * @param sep The string separator to apply. Should not be {@code null}.
     * @return The serialised set as string.
     */
    public static String serialize(final Set<String> set, final String sep) {

        StringBuilder sb = new StringBuilder();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(sep);
            }
        }

        return sb.toString();
    }

    /**
     * Parses a header value consisting of zero or more space / comma / space + comma separated strings. The input
     * string is trimmed before splitting.
     *
     * @param headerValue The header value, may be {@code null}.
     * @return A string array of the parsed string items, empty if none were found or the input was {@code null}.
     */
    public static String[] parseMultipleHeaderValues(final String headerValue) {

        if (headerValue == null) {
            return new String[0]; // empty array
        }

        String trimmedHeaderValue = headerValue.trim();

        if (trimmedHeaderValue.isEmpty()) {
            return new String[0];
        }

        return trimmedHeaderValue.split("\\s*,\\s*|\\s+");
    }

    /**
     * Applies a {@code Aaa-Bbb-Ccc} format to a header name.
     *
     * @param name The header name to format, must not be an empty string or {@code null}.
     * @return The formatted header name.
     * @throws IllegalArgumentException On a empty or invalid header name.
     */
    public static String formatCanonical(final String name) {

        String nameTrimmed = name.trim();
        if (nameTrimmed.isEmpty()) {
            throw new IllegalArgumentException("The header field name must not be an empty string");
        }

        // Check for valid syntax
        if (!VALID.matcher(nameTrimmed).matches()) {
            throw new IllegalArgumentException("Invalid header field name syntax (see RFC 2616)");
        }

        String[] tokens = nameTrimmed.toLowerCase().split("-");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            char[] c = tokens[i].toCharArray();
            c[0] = Character.toUpperCase(c[0]);  // Capitalise first char
            if (i >= 1) {
                out.append("-");
            }
            out.append(new String(c));
        }

        return out.toString();
    }
}

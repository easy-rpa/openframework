package eu.easyrpa.openframework.core.sevices;

/**
 * Allows to use within EasyRPA Open Framework provided by RPA platform services like configuration, secret vault, etc.
 * <p>
 * Each RPA platform where EasyRPA Open Framework are going to be used should provide own implementation
 * of this interface.
 */
public interface RPAServicesAccessor {

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found.
     */
    String getConfigParam(String key);

    /**
     * Gets value of secret vault entry specified in the Secret Vault of RPA platform.
     * <p>
     * It's expected that values of secret vault entries are stored in JSON format and can be mapped to Java objects af
     * type {@link T}.
     *
     * @param alias the alias string that is used as key to find necessary secret vault entry.
     * @param cls   the class of Java object to which the JSON value of necessary secret vault entry should be mapped to.
     * @param <T>   the type of secret value to return.
     * @return the value of secret vault entry as Java object of specified type.
     */
    <T> T getSecret(String alias, Class<T> cls);
}

package org.particleframework.inject.configproperties

import org.particleframework.config.ConfigurationProperties
import org.particleframework.context.ApplicationContext
import org.particleframework.context.DefaultApplicationContext
import org.particleframework.context.env.MapPropertySource
import spock.lang.Specification

/**
 * Created by graemerocher on 13/06/2017.
 */
class ConfigurationPropertiesSpec extends Specification {

    void "test configuration properties binding"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(new MapPropertySource(
            'foo.bar.port':'8080',
            'foo.bar.anotherPort':'9090',
            'foo.bar.intList':"1,2,3",
            'foo.bar.stringList':"1,2",
            'foo.bar.inner.enabled':'true',
            'foo.bar.flags.one':'1',
            'foo.bar.flags.two':'2',
            'foo.bar.urlList':"http://test.com, http://test2.com",
            'foo.bar.urlList2':["http://test.com", "http://test2.com"],
            'foo.bar.url':'http://test.com'
        ))

        applicationContext.start()

        MyConfig config = applicationContext.getBean(MyConfig)

        expect:
        config.port == 8080

        config.anotherPort == 9090
        config.intList == [1,2,3]
        config.flags == [one:1, two:2]
        config.urlList == [new URL('http://test.com'),new URL('http://test2.com')]
        config.urlList2 == [new URL('http://test.com'),new URL('http://test2.com')]
        config.stringList == ["1", "2"]
        config.emptyList == null
        config.url.get() == new URL('http://test.com')
        !config.anotherUrl.isPresent()
        config.defaultPort == 9999
        config.defaultValue == 9999
        config.primitiveDefaultValue == 9999
//        config.inner.enabled
    }

    @ConfigurationProperties('foo.bar')
    static class MyConfig {
        int port
        Integer defaultValue = 9999
        int primitiveDefaultValue = 9999
        protected int defaultPort = 9999
        protected Integer anotherPort
        List<String> stringList
        List<Integer> intList
        List<URL> urlList
        List<URL> urlList2
        List<URL> emptyList
        Map<String,Integer> flags
        Optional<URL> url
        Optional<URL> anotherUrl
////        Inner inner
//
        Integer getAnotherPort() {
            return anotherPort
        }

        int getDefaultPort() {
            return defaultPort
        }
//
//        static class Inner {
//            boolean enabled
//        }
    }
}

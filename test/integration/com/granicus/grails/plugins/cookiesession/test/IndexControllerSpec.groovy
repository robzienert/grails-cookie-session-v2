package com.granicus.grails.plugins.cookiesession.test

import com.granicus.grails.plugins.cookiesession.CookieSessionFilter
import com.granicus.grails.plugins.cookiesession.SessionRepository
import grails.plugin.spock.ControllerSpec
import grails.test.mixin.TestFor

import javax.servlet.FilterChain

@TestFor(IndexController)
class IndexControllerSpec extends ControllerSpec {

    def 'filter does not run when plugin disabled'() {
        given: 'plugin is disabled'
        grailsApplication.config.grails.plugin.cookiesession.enabled = false
        grailsApplication.configChanged()

        and: 'a cookie session filter'
        CookieSessionFilter filter = new CookieSessionFilter()
        filter.grailsApplication = grailsApplication

        FilterChain filterChain = Mock()

        when: 'running the filter'
        filter.doFilter(mockRequest, mockResponse, filterChain)

        then: 'cookie session internals are not called'
        noExceptionThrown()
        1 * filterChain.doFilter(_, _)
        0 * _
    }

    def 'filter runs when plugin enabled'() {
        given: 'plugin is enabled'
        grailsApplication.config.grails.plugin.cookiesession.enabled = true
        grailsApplication.configChanged()

        and: 'a cookie session filter'
        SessionRepository sessionRepository = Mock()

        CookieSessionFilter filter = new CookieSessionFilter()
        filter.grailsApplication = grailsApplication
        filter.sessionRepository = sessionRepository
        filter.@sessionPersistenceListeners = []

        FilterChain filterChain = Mock()

        when: 'running the filter'
        filter.doFilter(mockRequest, mockResponse, filterChain)

        then: 'cookie session internals are called'
        noExceptionThrown()
        1 * filterChain.doFilter(_, _)
        1 * sessionRepository.restoreSession(_)
        0 * _
    }
}

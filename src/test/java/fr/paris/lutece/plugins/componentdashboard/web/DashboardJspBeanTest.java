/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.componentdashboard.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboard;
import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboardHome;
import fr.paris.lutece.plugins.publicdashboard.web.PublicDashboardJspBean;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;

/**
 * This is the business class test for the object Dashboard
 */
public class DashboardJspBeanTest extends LuteceTestCase
{
    private static final int IDBEAN1 = 1;
    private static final int IDBEAN2 = 2;
    private static final int ZONE1 = 1;
    private static final int ZONE2 = 2;
    private static final int ORDER1 = 1;
    private static final int ORDER2 = 2;

    public void testJspBeans( ) throws AccessDeniedException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        MockServletConfig config = new MockServletConfig( );

        // display admin Dashboard management JSP
        PublicDashboardJspBean jspbean = new PublicDashboardJspBean( );
        String html = jspbean.getManageDashboards( request );
        assertNotNull( html );

        // display admin Dashboard creation JSP
        html = jspbean.getCreateDashboard( request );
        assertNotNull( html );

        // action create Dashboard
        request = new MockHttpServletRequest( );

        response = new MockHttpServletResponse( );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "id_bean", String.valueOf( IDBEAN1 ) );
        request.addParameter( "zone", String.valueOf( ZONE1 ) );
        request.addParameter( "order", String.valueOf( ORDER1 ) );
        request.addParameter( "action", "createDashboard" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createDashboard" ) );
        request.setMethod( "POST" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // display modify Dashboard JSP
        request = new MockHttpServletRequest( );
        request.addParameter( "id_bean", String.valueOf( IDBEAN1 ) );
        request.addParameter( "zone", String.valueOf( ZONE1 ) );
        request.addParameter( "order", String.valueOf( ORDER1 ) );
        List<Integer> listIds = PublicDashboardHome.getIdDashboardsList( );
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new PublicDashboardJspBean( );

        assertNotNull( jspbean.getModifyDashboard( request ) );

        // action modify Dashboard
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );

        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "id_bean", String.valueOf( IDBEAN2 ) );
        request.addParameter( "zone", String.valueOf( ZONE2 ) );
        request.addParameter( "order", String.valueOf( ORDER2 ) );
        request.setRequestURI( "jsp/admin/plugins/example/ManageDashboards.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createDashboard, qui est l'action par défaut
        request.addParameter( "action", "modifyDashboard" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyDashboard" ) );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // get remove Dashboard
        request = new MockHttpServletRequest( );
        // request.setRequestURI("jsp/admin/plugins/example/ManageDashboards.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new PublicDashboardJspBean( );
        request.addParameter( "action", "confirmRemoveDashboard" );
        assertNotNull( jspbean.getModifyDashboard( request ) );

        // do remove Dashboard
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );
        request.setRequestURI( "jsp/admin/plugins/example/ManageDashboardts.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createDashboard, qui est l'action par défaut
        request.addParameter( "action", "removeDashboard" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeDashboard" ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        request.setMethod( "POST" );
        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

    }
}

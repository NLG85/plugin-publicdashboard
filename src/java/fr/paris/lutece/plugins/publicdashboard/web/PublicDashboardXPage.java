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

package fr.paris.lutece.plugins.publicdashboard.web;

import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboard;
import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboardHome;
import fr.paris.lutece.plugins.publicdashboard.service.PublicDashboardService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.util.AppException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Dashboard xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = "dashboard", pageTitleI18nKey = "publicdashboard.xpage.dashboard.pageTitle", pagePathI18nKey = "publicdashboard.xpage.dashboard.pagePathLabel" )
public class PublicDashboardXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_VIEW_DASHBOARDS = "/skin/plugins/publicdashboard/view_dashboards.html";

    // Markers
    private static final String MARK_LIST_DASHBOARDS_CONTENT = "dashboard_list_content";

    // Views
    private static final String VIEW_GET_DASHBOARDS = "getDashboards";

    /**
     * return the form to manage dashboards
     * 
     * @param request
     *            The Http request
     * @return the html code of the list of dashboards
     */
    @View( value = VIEW_GET_DASHBOARDS, defaultView = true )
    public XPage getManageDashboards( HttpServletRequest request )
    {

        Map<String, Object> model = getModel( );

        List<PublicDashboard> lstDashboard = PublicDashboardHome.getDashboardsListByPosition( );
        List<String> listDashboardContent = new ArrayList<String>( lstDashboard.size( ) );

        for ( PublicDashboard dash : lstDashboard )
        {
            IPublicDashboardComponent dashboard = SpringContextService.getBean( dash.getIdBean( ) );
            if ( dashboard != null )
            {
                listDashboardContent.add( dashboard.getDashboardData( request ) );
            }
        }

        model.put( MARK_LIST_DASHBOARDS_CONTENT, listDashboardContent );

        return getXPage( TEMPLATE_VIEW_DASHBOARDS, getLocale( request ), model );
    }
}

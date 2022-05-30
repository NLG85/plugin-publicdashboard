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

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboard;
import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboardHome;
import fr.paris.lutece.portal.business.dashboard.IDashboardDAO;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.dashboard.IDashboardComponent;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Dashboard features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDashboards.jsp", controllerPath = "jsp/admin/plugins/publicdashboard/", right = "COMPONENTDASHBOARD_MANAGEMENT" )
public class PublicDashboardJspBean extends AbstractManagePublicDashboardJspBean<Integer, PublicDashboard>
{
    // Templates
    private static final String TEMPLATE_MANAGE_DASHBOARDS = "/admin/plugins/publicdashboard/manage_dashboards.html";
    private static final String TEMPLATE_CREATE_DASHBOARD = "/admin/plugins/publicdashboard/create_dashboard.html";
    private static final String TEMPLATE_MODIFY_DASHBOARD = "/admin/plugins/publicdashboard/modify_dashboard.html";

    // Parameters
    private static final String PARAMETER_ID_DASHBOARD = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DASHBOARDS = "componentdashboard.manage_dashboards.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DASHBOARD = "componentdashboard.modify_dashboard.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DASHBOARD = "componentdashboard.create_dashboard.pageTitle";

    // Markers
    private static final String MARK_DASHBOARD_LIST = "dashboard_list";
    private static final String MARK_DASHBOARD = "dashboard";
    private static final String MARK_LIST_DASHBOARD = "listtypedashboard";

    private static final String JSP_MANAGE_DASHBOARDS = "jsp/admin/plugins/publicdashboard/ManageDashboards.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DASHBOARD = "componentdashboard.message.confirmRemoveDashboard";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "componentdashboard.model.entity.dashboard.attribute.";

    // Views
    private static final String VIEW_MANAGE_DASHBOARDS = "manageDashboards";
    private static final String VIEW_CREATE_DASHBOARD = "createDashboard";
    private static final String VIEW_MODIFY_DASHBOARD = "modifyDashboard";

    // Actions
    private static final String ACTION_CREATE_DASHBOARD = "createDashboard";
    private static final String ACTION_MODIFY_DASHBOARD = "modifyDashboard";
    private static final String ACTION_REMOVE_DASHBOARD = "removeDashboard";
    private static final String ACTION_CONFIRM_REMOVE_DASHBOARD = "confirmRemoveDashboard";
    private static final String ACTION_MOVE_UP_DASHBOARD = "moveUpDashboard";
    private static final String ACTION_MOVE_DOWN_DASHBOARD = "moveDownDashboard";

    // Infos
    private static final String INFO_DASHBOARD_CREATED = "componentdashboard.info.dashboard.created";
    private static final String INFO_DASHBOARD_UPDATED = "componentdashboard.info.dashboard.updated";
    private static final String INFO_DASHBOARD_REMOVED = "componentdashboard.info.dashboard.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    private static final String PARAMETER_ID = "id";

    // Session variable to store working values
    private PublicDashboard _dashboard;
    private List<Integer> _listIdDashboards;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DASHBOARDS, defaultView = true )
    public String getManageDashboards( HttpServletRequest request )
    {
        _dashboard = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdDashboards.isEmpty( ) )
        {
            _listIdDashboards = PublicDashboardHome.getIdDashboardsListByPosition( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DASHBOARD_LIST, _listIdDashboards, JSP_MANAGE_DASHBOARDS );

        List<IPublicDashboardComponent> lstDashboard = SpringContextService.getBeansOfType( IPublicDashboardComponent.class );
        Map<String, String> mapDashboard = new HashMap<String, String>( );

        for ( IPublicDashboardComponent dash : lstDashboard )
        {
            mapDashboard.put( dash.getComponentId( ), dash.getComponentDescription( ) );
        }

        model.put( MARK_LIST_DASHBOARD, mapDashboard );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DASHBOARDS, TEMPLATE_MANAGE_DASHBOARDS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<PublicDashboard> getItemsFromIds( List<Integer> listIds )
    {
        List<PublicDashboard> listDashboard = PublicDashboardHome.getDashboardsListByIds( listIds );

        // keep original order
        return listDashboard.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdDashboards list
     */
    public void resetListId( )
    {
        _listIdDashboards = new ArrayList<>( );
    }

    /**
     * Returns the form to create a dashboard
     *
     * @param request
     *            The Http request
     * @return the html code of the dashboard form
     */
    @View( VIEW_CREATE_DASHBOARD )
    public String getCreateDashboard( HttpServletRequest request )
    {
        _dashboard = ( _dashboard != null ) ? _dashboard : new PublicDashboard( );

        Map<String, Object> model = getModel( );

        List<IPublicDashboardComponent> lstDashboard = SpringContextService.getBeansOfType( IPublicDashboardComponent.class );
        ReferenceList reflstDashboard = new ReferenceList( );

        for ( IPublicDashboardComponent dash : lstDashboard )
        {
            reflstDashboard.addItem( dash.getComponentId( ), dash.getComponentDescription( ) );
        }

        model.put( MARK_DASHBOARD, _dashboard );
        model.put( MARK_LIST_DASHBOARD, reflstDashboard );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DASHBOARD ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DASHBOARD, TEMPLATE_CREATE_DASHBOARD, model );
    }

    /**
     * Move up component
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display modify panel
     */
    @Action( ACTION_MOVE_UP_DASHBOARD )
    public String doMoveUpComponent( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );

        Optional<PublicDashboard> opt_public_dashboard = PublicDashboardHome.findByPrimaryKey( nId );

        if ( opt_public_dashboard.isPresent( ) )
        {
            PublicDashboard publicDashboardSelected = opt_public_dashboard.get( );
            List<PublicDashboard> listDashboard = PublicDashboardHome.getDashboardsListByPosition( );

            int nbPosition = 0;

            for ( PublicDashboard dashboard : listDashboard )
            {
                if ( ( dashboard.getId( ) == publicDashboardSelected.getId( ) ) && ( nbPosition != 0 ) )
                {
                    PublicDashboard dashboardPrev = listDashboard.get( nbPosition - 1 );
                    int nNewPosition = dashboardPrev.getPosition( );
                    dashboardPrev.setPosition( publicDashboardSelected.getPosition( ) );
                    publicDashboardSelected.setPosition( nNewPosition );
                    PublicDashboardHome.update( dashboardPrev );
                    PublicDashboardHome.update( publicDashboardSelected );
                }

                nbPosition++;
            }
        }

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

    /**
     * move down component
     *
     * @param request
     *            The Http request
     * @return The jsp URL to display modify panel
     */
    @Action( ACTION_MOVE_DOWN_DASHBOARD )
    public String doMoveDownComponent( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );

        Optional<PublicDashboard> opt_public_dashboard = PublicDashboardHome.findByPrimaryKey( nId );

        if ( opt_public_dashboard.isPresent( ) )
        {
            PublicDashboard publicDashboardSelected = opt_public_dashboard.get( );
            List<PublicDashboard> listDashboard = PublicDashboardHome.getDashboardsListByPosition( );
            int nbPosition = 0;

            for ( PublicDashboard dashboard : listDashboard )
            {
                if ( ( dashboard.getId( ) == publicDashboardSelected.getId( ) ) && ( nbPosition != ( listDashboard.size( ) - 1 ) ) )
                {
                    PublicDashboard dashboardNext = listDashboard.get( nbPosition + 1 );
                    int nNewPosition = dashboardNext.getPosition( );
                    dashboardNext.setPosition( publicDashboardSelected.getPosition( ) );
                    publicDashboardSelected.setPosition( nNewPosition );
                    PublicDashboardHome.update( dashboardNext );
                    PublicDashboardHome.update( publicDashboardSelected );
                }

                nbPosition++;
            }
        }

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

    /**
     * Process the data capture form of a new dashboard
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DASHBOARD )
    public String doCreateDashboard( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _dashboard, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DASHBOARD ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _dashboard, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DASHBOARD );
        }

        PublicDashboardHome.create( _dashboard );
        addInfo( INFO_DASHBOARD_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

    /**
     * Manages the removal form of a dashboard whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DASHBOARD )
    public String getConfirmRemoveDashboard( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DASHBOARD ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DASHBOARD ) );
        url.addParameter( PARAMETER_ID_DASHBOARD, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DASHBOARD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a dashboard
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage dashboards
     */
    @Action( ACTION_REMOVE_DASHBOARD )
    public String doRemoveDashboard( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DASHBOARD ) );

        PublicDashboardHome.remove( nId );
        addInfo( INFO_DASHBOARD_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

    /**
     * Returns the form to update info about a dashboard
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DASHBOARD )
    public String getModifyDashboard( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DASHBOARD ) );

        if ( _dashboard == null || ( _dashboard.getId( ) != nId ) )
        {
            Optional<PublicDashboard> optDashboard = PublicDashboardHome.findByPrimaryKey( nId );
            _dashboard = optDashboard.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );

        List<IPublicDashboardComponent> lstDashboard = SpringContextService.getBeansOfType( IPublicDashboardComponent.class );
        ReferenceList reflstDashboard = new ReferenceList( );

        for ( IPublicDashboardComponent dash : lstDashboard )
        {
            reflstDashboard.addItem( dash.getComponentId( ), dash.getComponentDescription( ) );
        }

        model.put( MARK_DASHBOARD, _dashboard );
        model.put( MARK_LIST_DASHBOARD, reflstDashboard );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DASHBOARD ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DASHBOARD, TEMPLATE_MODIFY_DASHBOARD, model );
    }

    /**
     * Process the change form of a dashboard
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DASHBOARD )
    public String doModifyDashboard( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _dashboard, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DASHBOARD ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _dashboard, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DASHBOARD, PARAMETER_ID_DASHBOARD, _dashboard.getId( ) );
        }

        PublicDashboardHome.update( _dashboard );
        addInfo( INFO_DASHBOARD_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }
}

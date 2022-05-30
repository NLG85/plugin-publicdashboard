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
package fr.paris.lutece.plugins.publicdashboard.service;

import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.security.RsaService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * The Class PublicDashboardService.
 */
public class PublicDashboardService
{

    public static final String PROPERTY_ENCRYPT = "publicdashboard.encrypt";

    public static final String PAGE_NAME = "dashboard";
    public static final String PARAMETER_USER_ID = "id_user";

    /**
     * Encrypt guid.
     *
     * @param guid
     *            the guid
     * @return the string
     */
    public static String encryptGuid( String guid )
    {
        if ( AppPropertiesService.getPropertyBoolean( PublicDashboardService.PROPERTY_ENCRYPT, false ) )
        {
            try
            {
                return RsaService.decryptRsa( guid );
            }
            catch( GeneralSecurityException e )
            {
                AppLogService.error( "Cannot decrypt " + guid, e );
            }
        }

        return guid;
    }

    /**
     * Gets the url.
     *
     * @param userid
     *            the userid
     * @return the url
     * @throws GeneralSecurityException
     *             the general security exception
     */
    public static String getUrl( String userid, HttpServletRequest request ) throws GeneralSecurityException
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, PAGE_NAME );
        if ( AppPropertiesService.getPropertyBoolean( PROPERTY_ENCRYPT, false ) )
        {
            url.addParameter( PARAMETER_USER_ID, RsaService.encryptRsa( userid ) );
        }
        else
        {
            url.addParameter( PARAMETER_USER_ID, userid );
        }

        return url.getUrl( );
    }

}

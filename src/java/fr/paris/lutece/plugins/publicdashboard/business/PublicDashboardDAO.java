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

package fr.paris.lutece.plugins.publicdashboard.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Dashboard objects
 */
public final class PublicDashboardDAO implements IPublicDashboardDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_dashboard, id_bean, zone, position FROM componentdashboard_dashboard WHERE id_dashboard = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO componentdashboard_dashboard ( id_bean, zone, position ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM componentdashboard_dashboard WHERE id_dashboard = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE componentdashboard_dashboard SET id_dashboard = ?, id_bean = ?, zone = ?, position = ? WHERE id_dashboard = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_dashboard, id_bean, zone, position FROM componentdashboard_dashboard";
    private static final String SQL_QUERY_SELECTALL_BY_POSITION = "SELECT id_dashboard, id_bean, zone, position FROM componentdashboard_dashboard ORDER BY position";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_dashboard FROM componentdashboard_dashboard";
    private static final String SQL_QUERY_SELECTALL_ID_BY_POSITION = "SELECT id_dashboard FROM componentdashboard_dashboard ORDER BY position";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_dashboard, id_bean, zone, position FROM componentdashboard_dashboard WHERE id_dashboard IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( PublicDashboard dashboard, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, dashboard.getIdBean( ) );
            daoUtil.setInt( nIndex++, dashboard.getZone( ) );
            daoUtil.setInt( nIndex++, dashboard.getPosition( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                dashboard.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<PublicDashboard> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            PublicDashboard dashboard = null;

            if ( daoUtil.next( ) )
            {
                dashboard = new PublicDashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdBean( daoUtil.getString( nIndex++ ) );
                dashboard.setZone( daoUtil.getInt( nIndex++ ) );
                dashboard.setPosition( daoUtil.getInt( nIndex ) );
            }

            return Optional.ofNullable( dashboard );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( PublicDashboard dashboard, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, dashboard.getId( ) );
            daoUtil.setString( nIndex++, dashboard.getIdBean( ) );
            daoUtil.setInt( nIndex++, dashboard.getZone( ) );
            daoUtil.setInt( nIndex++, dashboard.getPosition( ) );
            daoUtil.setInt( nIndex, dashboard.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<PublicDashboard> selectDashboardsList( Plugin plugin )
    {
        List<PublicDashboard> dashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                PublicDashboard dashboard = new PublicDashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdBean( daoUtil.getString( nIndex++ ) );
                dashboard.setZone( daoUtil.getInt( nIndex++ ) );
                dashboard.setPosition( daoUtil.getInt( nIndex ) );

                dashboardList.add( dashboard );
            }

            return dashboardList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDashboardsList( Plugin plugin )
    {
        List<Integer> dashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                dashboardList.add( daoUtil.getInt( 1 ) );
            }

            return dashboardList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectDashboardsReferenceList( Plugin plugin )
    {
        ReferenceList dashboardList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                dashboardList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return dashboardList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<PublicDashboard> selectDashboardsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<PublicDashboard> dashboardList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    PublicDashboard dashboard = new PublicDashboard( );
                    int nIndex = 1;

                    dashboard.setId( daoUtil.getInt( nIndex++ ) );
                    dashboard.setIdBean( daoUtil.getString( nIndex++ ) );
                    dashboard.setZone( daoUtil.getInt( nIndex++ ) );
                    dashboard.setPosition( daoUtil.getInt( nIndex ) );

                    dashboardList.add( dashboard );
                }

                daoUtil.free( );

            }
        }
        return dashboardList;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<PublicDashboard> selectDashboardsListByPosition( Plugin plugin )
    {
        List<PublicDashboard> publicDashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_POSITION, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                PublicDashboard dashboard = new PublicDashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdBean( daoUtil.getString( nIndex++ ) );
                dashboard.setZone( daoUtil.getInt( nIndex++ ) );
                dashboard.setPosition( daoUtil.getInt( nIndex ) );

                publicDashboardList.add( dashboard );
            }

            daoUtil.free( );
            return publicDashboardList;
        }
    }

    @Override
    public List<Integer> selectIdDashboardsListByPosition( Plugin plugin )
    {
        List<Integer> dashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_BY_POSITION, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                dashboardList.add( daoUtil.getInt( 1 ) );
            }

            return dashboardList;
        }
    }
}

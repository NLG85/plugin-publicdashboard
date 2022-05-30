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
package fr.paris.lutece.plugins.componentdashboard.business;

import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboard;
import fr.paris.lutece.plugins.publicdashboard.business.PublicDashboardHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

/**
 * This is the business class test for the object Dashboard
 */
public class DashboardBusinessTest extends LuteceTestCase
{
    private static final String IDBEAN1 = "1";
    private static final String IDBEAN2 = "2";
    private static final int ZONE1 = 1;
    private static final int ZONE2 = 2;
    private static final int ORDER1 = 1;
    private static final int ORDER2 = 2;

    /**
     * test Dashboard
     */
    public void testBusiness( )
    {
        // Initialize an object
        PublicDashboard dashboard = new PublicDashboard( );
        dashboard.setIdBean( IDBEAN1 );
        dashboard.setZone( ZONE1 );
        dashboard.setPosition( ORDER1 );

        // Create test
        PublicDashboardHome.create( dashboard );
        Optional<PublicDashboard> optDashboardStored = PublicDashboardHome.findByPrimaryKey( dashboard.getId( ) );
        PublicDashboard dashboardStored = optDashboardStored.orElse( new PublicDashboard( ) );
        assertEquals( dashboardStored.getIdBean( ), dashboard.getIdBean( ) );
        assertEquals( dashboardStored.getZone( ), dashboard.getZone( ) );
        assertEquals( dashboardStored.getPosition( ), dashboard.getPosition( ) );

        // Update test
        dashboard.setIdBean( IDBEAN2 );
        dashboard.setZone( ZONE2 );
        dashboard.setPosition( ORDER2 );
        PublicDashboardHome.update( dashboard );
        optDashboardStored = PublicDashboardHome.findByPrimaryKey( dashboard.getId( ) );
        dashboardStored = optDashboardStored.orElse( new PublicDashboard( ) );

        assertEquals( dashboardStored.getIdBean( ), dashboard.getIdBean( ) );
        assertEquals( dashboardStored.getZone( ), dashboard.getZone( ) );
        assertEquals( dashboardStored.getPosition( ), dashboard.getPosition( ) );

        // List test
        PublicDashboardHome.getDashboardsList( );

        // Delete test
        PublicDashboardHome.remove( dashboard.getId( ) );
        optDashboardStored = PublicDashboardHome.findByPrimaryKey( dashboard.getId( ) );
        dashboardStored = optDashboardStored.orElse( null );
        assertNull( dashboardStored );

    }

}

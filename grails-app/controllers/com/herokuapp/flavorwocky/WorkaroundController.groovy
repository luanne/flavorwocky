/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
package com.herokuapp.flavorwocky

/**
 * the facebook sdk trips when a url is mapped directly to a view without a controller. therefore, this...
 */
class WorkaroundController {

    def about() {
        render view: '/about.gsp'
    }

    def help() {
        render view: '/help.gsp'
    }
}

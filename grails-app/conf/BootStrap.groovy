/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
import com.herokuapp.flavorwocky.Category

class BootStrap {

    /**
     * Initialize the app
     */
    def init = { servletContext ->
        //check if there are categories in the db
        if (Category.count()<=0) {
            createInitialCategories()
        }
    }

    def destroy = {
    }

    /**
     * Create categories and link to the reference node.
     */
    private void createInitialCategories() {
        //todo: is this really being batched? need to verify
        Category.withTransaction {
            [
                    'Fish':'darkblue',
                    'Poultry':'hotpink',
                    'Meat':'firebrick',
                    'Herbs and spices':'yellowgreen',
                    'Condiments':'goldenrod',
                    'Eggs and dairy':'wheat',
                    'Vegetables':'darkgreen',
                    'Fruits':'lightcoral',
                    'Nuts and Grains':'orange',
                    'Chocolate, Bread and Pastry':'saddlebrown'
            ].each {
                new Category(name: it.key, catColor: it.value).save(failOnError: true)
            }
        }
    }

}

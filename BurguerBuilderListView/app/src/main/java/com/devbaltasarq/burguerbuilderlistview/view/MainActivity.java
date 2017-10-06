package com.devbaltasarq.burguerbuilderlistview.view;

import android.content.DialogInterface;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.devbaltasarq.burguerbuilderlistview.R;
import com.devbaltasarq.burguerbuilderlistview.core.BurguerConfigurator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the burguer configuration
        this.cfgBurguer = new BurguerConfigurator();

        // Report relevant info
        Log.i( "MainActivity.OnCreate", "Number of ingredients: "
                + BurguerConfigurator.INGREDIENTS.length );

        for(int i = 0; i < BurguerConfigurator.INGREDIENTS.length; ++i) {
            Log.i( "MainActivity.OnCreate", "Availabel ingredient: "
                    + BurguerConfigurator.INGREDIENTS[ i ] );
        }

        // Create callback for the button allowing to check prices
        Button btPrices = (Button) this.findViewById( R.id.btPrices );
        btPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showPricesDialog();
            }
        });

        // Create callback for the button allowing to select ingredients
        Button btIngredients = (Button) this.findViewById( R.id.btIngredients );
        btIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showIngredientsDialog();
            }
        });

        this.showFixedIngredients();
        this.updateTotals();
    }

    private void showFixedIngredients()
    {
        ListView lvFixedIngredients = (ListView) this.findViewById( R.id.lvFixedIngredients );

        lvFixedIngredients.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        new String[] { "1.00€ Pan", "2.00€ Carne de vacuno" } ) );

    }

    private void showIngredients()
    {
        final boolean[] selections = this.cfgBurguer.getSelected();
        final ListView lvIngredients = (ListView) this.findViewById( R.id.lvIngredients );

        // Create list
        ArrayList<String> ingredients = new ArrayList<>();
        for(int i = 0; i < this.cfgBurguer.getSelected().length; ++i) {
            if ( selections[ i ] ) {
                ingredients.add(
                        String.format( "%4.2f€", BurguerConfigurator.COSTS[ i ] )
                        + " " + BurguerConfigurator.INGREDIENTS[ i ] );
            }
        }

        lvIngredients.setLongClickable( true );
        lvIngredients.setClickable( false );
        lvIngredients.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_selectable_list_item,
                        ingredients.toArray( new String[ ingredients.size() ] ) ) );
        lvIngredients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int listPos = MainActivity.this.cfgBurguer.getGlobalPosOfSelected( i );
System.out.println( String.format( "Selected %d, global %d: %s", i, listPos, BurguerConfigurator.INGREDIENTS[ listPos ] ) );
                MainActivity.this.cfgBurguer.getSelected()[ listPos ] = false;
                MainActivity.this.showIngredients();
                MainActivity.this.updateTotals();
                return true;
            }
        });
    }

    private void updateTotals()
    {
        final TextView lblTotal = (TextView) this.findViewById( R.id.lblTotal );

        // Report
        Log.i( "MainActivity.updTotals", "Starting updating." );
        for(int i = 0; i < BurguerConfigurator.INGREDIENTS.length; ++i) {
            Log.i( "MainActivity.updTotals",
                    BurguerConfigurator.INGREDIENTS[ i ]
                            + " (" + BurguerConfigurator.COSTS[ i ] + ")"
                            + ": " + ( this.cfgBurguer.getSelected()[ i ] ? "Yes" : "No" )
            );
        }

        Log.i( "MainActivity.updTotals", "Total cost: "  + this.cfgBurguer.calculateCost() );

        // Update
        lblTotal.setText(
                String.format( "%4.2f", MainActivity.this.cfgBurguer.calculateCost() ) );
        this.showIngredients();
        Log.i( "MainActivity.updTotals", "End updating." );
    }

    private void showIngredientsDialog()
    {
        final boolean[] selections = this.cfgBurguer.getSelected();
        AlertDialog.Builder dlg = new AlertDialog.Builder( this );

        dlg.setTitle( this.getResources().getString( R.string.lblIngredientSelection) );

        dlg.setMultiChoiceItems(
                BurguerConfigurator.INGREDIENTS,
                selections,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selections[ i ] = b;
                    }
                }
        );

        dlg.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.updateTotals();
            }
        });
        dlg.create().show();
    }

    private void showPricesDialog()
    {
        final String[] ingredientsWithPrices = new String[ BurguerConfigurator.getNumIngredients() ];
        final TextView lblData = new TextView( this );
        AlertDialog.Builder dlg = new AlertDialog.Builder( this );
        dlg.setTitle( this.getResources().getString( R.string.lblPrices) );

        // Build list with prices
        for(int i = 0; i < ingredientsWithPrices.length; ++i) {
            ingredientsWithPrices[ i ] = String.format( "%4.2f€ %s",
                    BurguerConfigurator.COSTS[ i ],
                    BurguerConfigurator.INGREDIENTS[ i ] );
        }

        lblData.setText( String.join( "\n", ingredientsWithPrices ) );
        lblData.setPadding( 10, 10, 10, 10 );
        dlg.setView( lblData );
        dlg.setPositiveButton( "Ok", null );
        dlg.create().show();
    }

    private BurguerConfigurator cfgBurguer;
}

package com.example.mad

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mad.data.ShoppingListItem
import com.example.mad.ui.theme.MADTheme
import com.example.mad.viewmodel.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ShoppingListScaffold()
                }
            }
        }
    }
}

@Composable
fun ShoppingListScaffold() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        content = { innerPadding -> ShoppingListScreen(Modifier.padding(innerPadding)) },
    )
}

@Composable
fun ShoppingListScreen(
    modifier: Modifier,
    shoppingListViewModel: ShoppingListViewModel = viewModel()
) {
    val context = LocalContext.current

    /*
     * ArrayList to hold the shoppinglist data entered by the user.
     * Database of shoppinglist items is read here !
     */
    val shoppingListItems: List<ShoppingListItem>? by shoppingListViewModel.shoppingListItems.observeAsState()

    //default value is false - not showing any dialog
    val dialogState = remember { mutableStateOf(false) }

    //lambdas for state hoisting
    val onDeleteShoppingListItem = { shoppingListItem: ShoppingListItem ->
        shoppingListViewModel.deleteShoppingListItem(shoppingListItem)
    }

    val onDeleteAllShoppingListItems = {
        shoppingListViewModel.deleteAllShoppingListItems()
    }

//add this final lambda
    val onAddShoppingListItem = { shoppingListItem: ShoppingListItem ->
        shoppingListViewModel.insertShoppingListItem(shoppingListItem)
    }

    Column(
        modifier = modifier.fillMaxHeight(),
    ) {
        if (dialogState.value) {
            AddShoppingListItemDialog(
                context = context,
                dialogState,
                onAddShoppingListItem
            )
        }
        // since the shoppingListItems are nullable, pass a empty list when that actually is the case
        // ?: = elvis operator(look at it sideways and you see why :)
        ShoppingList(
            context = context,
            shoppingListItems ?: arrayListOf(),
            modifier = Modifier.weight(1f), //pass weight from here so compose knows on which parent to base the weight on
            onDeleteShoppingListItem
        )

        AddDeleteButtons(
            dialogState = dialogState,
            onDeleteAllShoppingListItem = onDeleteAllShoppingListItems
        )
    }
}

@Composable
private fun ShoppingList(
    context: Context,
    shoppingListItems: List<ShoppingListItem>,
    modifier: Modifier,
    onDeleteShoppingListItem: (ShoppingListItem) -> Unit
) {
    LazyColumn(modifier) {
        items(
            items = shoppingListItems,
            key = { shoppingListItem -> shoppingListItem.id },
            itemContent = { shoppingListItem ->
                Row(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                Toast.makeText(
                                    context,
                                    R.string.press_long_to_delete,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onLongPress = {
                                //call the lambda that's being passed! pass the current shoppingListItem to it
                                onDeleteShoppingListItem(shoppingListItem)
                            },
                        )
                    }
                ) {
                    Text(
                        text = "${shoppingListItem.amount}X",
                        Modifier.padding(end = 24.dp)
                    )
                    Text(
                        text = shoppingListItem.product,
                    )
                }
                Divider(
                    color = Color.LightGray, modifier = Modifier.alpha(0.6f),
                    thickness = 1.dp
                )
            })
    }
}

@Composable
private fun AddDeleteButtons(
    dialogState: MutableState<Boolean>,
    onDeleteAllShoppingListItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        FloatingActionButton(
            onClick = {
                dialogState.value = true
            },
            backgroundColor = Color(0xff4EAF52)

        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add item",
                tint = Color.White,
            )
        }

        FloatingActionButton(
            modifier = Modifier.padding(start = 16.dp),
            onClick = {
                onDeleteAllShoppingListItem
            },
            backgroundColor = Color(0xffe64a19)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete item",
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun AddShoppingListItemDialog(
    context: Context,
    dialogState: MutableState<Boolean>,
    onAddShoppingListItem: (item: ShoppingListItem) -> Unit,
) {
    // local state for values within dialog, getter/setter way of declaring state
    val (shoppingListName, setShoppingListName) = remember { mutableStateOf(String()) }
    val (shoppingListAmount, setShoppingListAmount) = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { dialogState.value = false },
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            elevation = 8.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    style = MaterialTheme.typography.h6,
                    text = stringResource(id = R.string.add_product_to_list),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = shoppingListName,
                    modifier = Modifier.padding(bottom = 16.dp),
                    placeholder = { Text(text = stringResource(id = R.string.add_product)) },
                    onValueChange = {
                        //set the state variable for the item name to the value of the text field
                        //notice the call of the setter
                        setShoppingListName(it)
                    },
                )

                OutlinedTextField(
                    value = shoppingListAmount,
                    //enforcing numeric input
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    placeholder = { Text(text = stringResource(id = R.string.amount)) },
                    onValueChange = {
                        //set the state variable for the item amount to the value of the text field
                        //notice the call of the setter
                        setShoppingListAmount(it)
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = {
                            //This onClick is only partly state hoisted, in order to maintain an acceptable
                            //complexity for education purposes

                            // validation logic first
                            if (shoppingListName.isNotEmpty()
                                && shoppingListAmount.isNotEmpty() && shoppingListAmount.isDigitsOnly()
                            ) {
                                //create an shoppinglistitem model, pass it to the lambda!
                                val shoppingListItem = ShoppingListItem(shoppingListAmount.toInt(), shoppingListName)
                                onAddShoppingListItem(shoppingListItem)

                                // let dialog disappear at the next recomposition
                                dialogState.value = false

                                //empty fields
                                setShoppingListName("")
                                setShoppingListAmount("")
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.empty_fields_err,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.okay))
                    }
                }
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MADTheme {
    }
}
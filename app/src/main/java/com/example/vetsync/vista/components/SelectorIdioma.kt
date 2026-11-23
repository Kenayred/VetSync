
package com.example.vetsync.vista.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.R
import com.example.vetsync.util.GestorIdioma

@Composable
fun SelectorIdioma(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    val idiomaActual = GestorIdioma.obtenerIdioma(context)

    // --------------------------------------------------
    // OPCIÓN DE IDIOMA
    // --------------------------------------------------

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                mostrarDialogo = true
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Outlined.Language,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = stringResource(R.string.language_title),
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = if (idiomaActual == GestorIdioma.ESPANOL) {
                    stringResource(R.string.language_spanish)
                } else {
                    stringResource(R.string.language_english)
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RadioButton(
            selected = false,
            onClick = {
                mostrarDialogo = true
            }
        )
    }

    // --------------------------------------------------
    // DIÁLOGO DE SELECCIÓN
    // --------------------------------------------------

    if (mostrarDialogo) {

        AlertDialog(

            onDismissRequest = {
                mostrarDialogo = false
            },

            title = {
                Text(
                    text = stringResource(
                        R.string.language_dialog_title
                    )
                )
            },

            text = {

                Column {

                    // Español.

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                mostrarDialogo = false

                                GestorIdioma.cambiarIdioma(
                                    context,
                                    GestorIdioma.ESPANOL
                                )
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = idiomaActual ==
                                    GestorIdioma.ESPANOL,
                            onClick = {

                                mostrarDialogo = false

                                GestorIdioma.cambiarIdioma(
                                    context,
                                    GestorIdioma.ESPANOL
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(
                                R.string.language_spanish
                            )
                        )
                    }

                    // Inglés.

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                mostrarDialogo = false

                                GestorIdioma.cambiarIdioma(
                                    context,
                                    GestorIdioma.INGLES
                                )
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = idiomaActual ==
                                    GestorIdioma.INGLES,
                            onClick = {

                                mostrarDialogo = false

                                GestorIdioma.cambiarIdioma(
                                    context,
                                    GestorIdioma.INGLES
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(
                                R.string.language_english
                            )
                        )
                    }
                }
            },

            confirmButton = {},

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarDialogo = false
                    }
                ) {

                    Text(
                        text = stringResource(
                            R.string.language_dialog_cancel
                        )
                    )
                }
            }
        )
    }
}

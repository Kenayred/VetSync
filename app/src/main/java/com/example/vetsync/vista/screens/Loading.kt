package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.vetsync.R
import com.example.vetsync.vista.components.LoadingDots
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro),
        contentAlignment = Alignment.Center
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_vetsync),
                        contentDescription = "Logo principal de VetSync",
                        modifier = Modifier.size(64.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(VerdeFondoIcono),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sec_logo_vetsync),
                        contentDescription = "Logo secundario de VetSync",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título principal
            Text(
                text = "VetSync",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerdeOscuro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuidado inteligente para tus\nmascotas",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Puntos de Carga
            LoadingDots()
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(VerdePrincipal))
//                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(VerdePrincipal))
//                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(VerdePrincipal))
//            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cargando...",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
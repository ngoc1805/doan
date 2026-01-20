package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun MenuCard(
    menu: com.example.dat_lich_kham_fe.data.model.Menu,
    quantity: Int,
    onAddToCart: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val url = "${com.example.dat_lich_kham_fe.data.api.address}/api/${menu.imageUrl}"

    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = menu.name,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menu.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = menu.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Danh mục: ${menu.category}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Giá và nút thêm vào giỏ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${menu.examPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )

                // Nút giỏ hàng với counter
                AnimatedVisibility(
                    visible = quantity > 0,
                    enter = scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn(),
                    exit = scaleOut(tween(200)) + fadeOut(tween(200))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp)
                    ) {
                        // Nút giảm
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                text = "−",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        // Số lượng
                        Text(
                            text = quantity.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )

                        // Nút tăng
                        IconButton(
                            onClick = onIncrease,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFF10B981),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Nút giỏ hàng (luôn hiển thị khi quantity = 0)
                AnimatedVisibility(
                    visible = quantity == 0,
                    enter = scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn(),
                    exit = scaleOut(tween(200)) + fadeOut(tween(200))
                ) {
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .background(
                                color = Color(0xFF3B82F6),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Thêm vào giỏ",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

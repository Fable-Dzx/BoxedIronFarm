#!/usr/bin/env python3
"""生成盒装刷铁机模组贴图：僵尸小人、村民小人、模组图标。"""
from PIL import Image, ImageDraw

OUT = "/home/user/Doubao/chats/38444305408431106/boxed-iron-farm/src/main/resources/assets/boxedironfarm/textures/block"

# ---------- 僵尸 16x16 ----------
z = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
d = ImageDraw.Draw(z)
GREEN = (76, 127, 54, 255)      # 僵尸绿
DARK_GREEN = (46, 74, 36, 255)  # 深绿（头发/阴影）
DARK = (28, 43, 20, 255)        # 眼睛/嘴
SHIRT = (63, 91, 102, 255)      # 衬衫灰蓝
SHIRT_D = (47, 70, 80, 255)
PANTS = (42, 66, 82, 255)
SHOES = (23, 34, 43, 255)

# 头
d.rectangle([2, 0, 13, 7], fill=GREEN)
d.rectangle([2, 0, 13, 1], fill=DARK_GREEN)
# 头发纹理
d.point([3, 1], fill=DARK_GREEN); d.point([12, 1], fill=DARK_GREEN)
# 眼睛
d.rectangle([4, 3, 5, 4], fill=DARK)
d.rectangle([10, 3, 11, 4], fill=DARK)
# 嘴
d.rectangle([5, 6, 10, 6], fill=DARK)
# 额头阴影
d.rectangle([5, 2, 10, 2], fill=(64, 105, 46, 255))
# 身体（衬衫）
d.rectangle([3, 8, 12, 11], fill=SHIRT)
d.rectangle([3, 8, 4, 11], fill=SHIRT_D)
d.rectangle([11, 8, 12, 11], fill=SHIRT_D)
# 破洞
d.rectangle([6, 9, 7, 9], fill=GREEN)
# 裤子
d.rectangle([4, 12, 11, 14], fill=PANTS)
# 鞋
d.rectangle([4, 15, 7, 15], fill=SHOES)
d.rectangle([9, 15, 12, 15], fill=SHOES)
z.save(f"{OUT}/zombie_figure.png")

# ---------- 村民 16x16 ----------
v = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
d = ImageDraw.Draw(v)
HAIR = (107, 66, 38, 255)      # 棕发
SKIN = (216, 168, 120, 255)    # 肤色
SKIN_D = (193, 148, 104, 255)
EYE = (74, 49, 24, 255)
NOSE = (201, 158, 110, 255)
ROBE = (139, 90, 43, 255)
ROBE_D = (110, 69, 34, 255)
LEGS = (62, 42, 24, 255)

# 头
d.rectangle([2, 0, 13, 7], fill=SKIN)
d.rectangle([2, 0, 13, 1], fill=HAIR)
d.rectangle([3, 2, 5, 2], fill=HAIR)
d.rectangle([10, 2, 12, 2], fill=HAIR)
# 眼睛
d.point([5, 4], fill=EYE)
d.point([10, 4], fill=EYE)
# 大鼻子
d.rectangle([7, 5, 8, 6], fill=NOSE)
# 嘴
d.rectangle([6, 7, 9, 7], fill=SKIN_D)
# 脸部两侧阴影
d.rectangle([2, 2, 2, 7], fill=SKIN_D)
d.rectangle([13, 2, 13, 7], fill=SKIN_D)
# 长袍
d.rectangle([3, 8, 12, 13], fill=ROBE)
d.rectangle([3, 10, 4, 13], fill=ROBE_D)
d.rectangle([11, 10, 12, 13], fill=ROBE_D)
# 腰带
d.rectangle([3, 10, 12, 10], fill=(61, 38, 22, 255))
# 腿
d.rectangle([4, 14, 7, 15], fill=LEGS)
d.rectangle([9, 14, 12, 15], fill=LEGS)
v.save(f"{OUT}/villager_figure.png")

# ---------- 模组图标 128x128 ----------
icon = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
d = ImageDraw.Draw(icon)
# 玻璃盒外框（浅蓝白半透明）
d.rounded_rectangle([10, 6, 118, 122], radius=10, fill=(180, 215, 235, 150), outline=(220, 240, 250, 255), width=4)
# 石头底部
d.rectangle([14, 96, 114, 120], fill=(125, 125, 125, 255))
d.rectangle([14, 96, 114, 100], fill=(150, 150, 150, 255))
# 岩浆池（中间）
d.ellipse([40, 82, 88, 112], fill=(255, 120, 20, 255))
d.ellipse([48, 90, 80, 106], fill=(255, 200, 60, 255))
# 岩浆浪花
d.ellipse([46, 80, 58, 90], fill=(255, 160, 40, 255))
d.ellipse([72, 84, 84, 94], fill=(255, 150, 30, 255))
# 左侧僵尸头（放大版 24x24）
d.rectangle([22, 30, 54, 62], fill=GREEN)
d.rectangle([22, 30, 54, 34], fill=DARK_GREEN)
d.rectangle([29, 40, 33, 44], fill=DARK)
d.rectangle([43, 40, 47, 44], fill=DARK)
d.rectangle([30, 52, 46, 54], fill=DARK)
d.rectangle([22, 62, 54, 70], fill=SHIRT)
d.rectangle([22, 62, 26, 70], fill=SHIRT_D)
d.rectangle([50, 62, 54, 70], fill=SHIRT_D)
d.rectangle([26, 70, 50, 78], fill=PANTS)
# 右侧村民头（放大版 24x24）
d.rectangle([74, 30, 106, 62], fill=SKIN)
d.rectangle([74, 30, 106, 34], fill=HAIR)
d.rectangle([76, 35, 80, 35], fill=HAIR)
d.rectangle([100, 35, 104, 35], fill=HAIR)
d.point([81, 42], fill=EYE)
d.point([99, 42], fill=EYE)
d.rectangle([87, 44, 93, 48], fill=NOSE)
d.rectangle([82, 54, 98, 56], fill=SKIN_D)
d.rectangle([74, 62, 106, 72], fill=ROBE)
d.rectangle([74, 66, 106, 66], fill=(61, 38, 22, 255))
d.rectangle([80, 72, 100, 80], fill=ROBE_D)
# 顶部铁锭装饰
d.rectangle([56, 12, 76, 20], fill=(230, 230, 235, 255), outline=(160, 160, 170, 255))
d.rectangle([58, 8, 74, 12], fill=(210, 210, 215, 255))
icon.save("/home/user/Doubao/chats/38444305408431106/boxed-iron-farm/src/main/resources/assets/boxedironfarm/icon.png")

print("textures OK")

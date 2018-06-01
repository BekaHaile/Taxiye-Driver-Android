/*     */ package product.clicklabs.jugnoo.driver.utils;
/*     */ 
/*     */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


/*     */
/*     */ public class ASSL
/*     */ {
    /*     */   ViewGroup rv;
    /*     */   private Context context;
    /*     */   static float scaleX;
    /*     */   static float scaleY;
    /*     */   float xx;
    /*     */   static float height;
    /*     */   static float width;
    /*  25 */   float density = 0.0F;
    /*     */   int heightp;
    /*     */   int widthp;
    /*     */
/*     */   public ASSL(Context context, ViewGroup rootLayout, int layoutHeight, int layoutWidth, Boolean isFullscrenn)
/*     */   {
/*  46 */     this.context = context;
/*  47 */     this.rv = rootLayout;
/*  48 */     this.heightp = layoutHeight;
/*  49 */     this.widthp = layoutWidth;
/*     */
/*  51 */     height = this.context.getResources().getDisplayMetrics().heightPixels;
/*  52 */     width = this.context.getResources().getDisplayMetrics().widthPixels;
/*  53 */     this.density = this.context.getResources().getDisplayMetrics().density;
/*     */
/*  55 */     if (!isFullscrenn.booleanValue()) {
/*  56 */       height -= (int)(24.0F * this.density);
/*     */     }
/*     */
/*  73 */       scale();
/*     */   }
    /*     */
    public ASSL(Context context, int layoutHeight, int layoutWidth, Boolean isFullscrenn)
/*     */   {
/*  46 */     this.context = context;
/*  47 */     this.rv = null;
/*  48 */     this.heightp = layoutHeight;
/*  49 */     this.widthp = layoutWidth;
/*     */
/*  51 */     height = this.context.getResources().getDisplayMetrics().heightPixels;
/*  52 */     width = this.context.getResources().getDisplayMetrics().widthPixels;
/*  53 */     this.density = this.context.getResources().getDisplayMetrics().density;
/*     */
/*  55 */     if (!isFullscrenn.booleanValue()) {
/*  56 */       height -= (int)(24.0F * this.density);
/*     */     }
/*     */       scaleX = width / this.widthp;
/* 107 */       scaleY = height / this.heightp;

/*     */   }

    /*     */   private void scale()
/*     */   {
/* 106 */     scaleX = width / this.widthp;
/* 107 */     scaleY = height / this.heightp;
/*     */
/* 119 */     DoMagic(this.rv);
/*     */   }
    /*     */
/*     */   public static float Xscale()
/*     */   {
/* 124 */     return scaleX;
/*     */   }

    public static float minRatio(){
        return Math.min(ASSL.Xscale(), ASSL.Yscale());
    }


    /*     */
/*     */   public static float Yscale() {
/* 128 */     return scaleY;
/*     */   }
    /*     */
/*     */   public static void closeActivity(View rootLayout)
/*     */   {
/* 135 */
        try {
            if (rootLayout.getBackground() != null) {
		/* 136 */       rootLayout.getBackground().setCallback(null);
		/*     */     }
/*     */
/* 139 */
            if (((rootLayout instanceof ViewGroup)) &&
		/* 140 */       (!(rootLayout instanceof AdapterView))) {
		/* 141 */       for (int i = 0; i < ((ViewGroup)rootLayout).getChildCount(); i++)
		/* 142 */         closeActivity(((ViewGroup)rootLayout).getChildAt(i));
		/*     */       try
		/*     */       {
		/* 145 */         ((ViewGroup)rootLayout).removeAllViews();
		/*     */       } catch (UnsupportedOperationException localUnsupportedOperationException) {
		/*     */       }
		/*     */     }
		/*     */     else {
		/* 150 */       rootLayout = null;
		/*     */     }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*     */   }
    /*     */
/*     */   public static void DoMagic(View rootLayout)
/*     */   {
        if(rootLayout.getTag() != null && rootLayout.getTag().toString().equals("DontTouchMe")){return;}
/* 168 */     if ((rootLayout instanceof TextView))
/*     */     {
/* 173 */       TextView tv = (TextView)rootLayout;
/*     */
/* 178 */       float newTextSize = tv.getTextSize() * scaleX;
/*     */
/* 180 */       tv.setTextSize(0, newTextSize);
/*     */     }

/*     */
/* 185 */     ViewGroup.LayoutParams layoutParams = rootLayout.getLayoutParams();
/*     */
/* 187 */     if (rootLayout.getTag() != null)
/*     */     {
/* 189 */       if (rootLayout.getTag().toString().equals("mwar"))
/*     */       {
/* 191 */         if ((layoutParams.width != -2) &&
/* 192 */           (layoutParams.width != -1) &&
/* 193 */           (layoutParams.width != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp80_79 = layoutParams; tmp80_79.width = ((int)(tmp80_79.width * scaleX));
/* 195 */         }if ((layoutParams.height != -2) &&
/* 196 */           (layoutParams.height != -1) &&
/* 197 */           (layoutParams.height != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp119_118 = layoutParams; tmp119_118.height = ((int)(tmp119_118.height * scaleX));
/*     */         }
/*     */
/*     */       }
/* 202 */       else if (rootLayout.getTag().toString().equals("mhar"))
/*     */       {
/* 204 */         if ((layoutParams.width != -2) &&
/* 205 */           (layoutParams.width != -1) &&
/* 206 */           (layoutParams.width != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp176_175 = layoutParams; tmp176_175.width = ((int)(tmp176_175.width * scaleY));
/* 208 */         }if ((layoutParams.height != -2) &&
/* 209 */           (layoutParams.height != -1) &&
/* 210 */           (layoutParams.height != -1))
/*     */         {
/*     */           ViewGroup.LayoutParams tmp215_214 = layoutParams; tmp215_214.height = ((int)(tmp215_214.height * scaleY));
/*     */         }
/*     */
/*     */       }
/* 215 */       else if (rootLayout.getTag().toString().equals("mlar"))
/*     */       {
/* 217 */         if (scaleY < scaleX) {
/* 218 */           if ((layoutParams.width != -2) &&
/* 219 */             (layoutParams.width != -1) &&
/* 220 */             (layoutParams.width != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp282_281 = layoutParams; tmp282_281.width = ((int)(tmp282_281.width * scaleY));
/* 222 */           }if ((layoutParams.height != -2) &&
/* 223 */             (layoutParams.height != -1) &&
/* 224 */             (layoutParams.height != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp321_320 = layoutParams; tmp321_320.height = ((int)(tmp321_320.height * scaleY));
/*     */           }
/*     */         } else { if ((layoutParams.width != -2) &&
/* 228 */             (layoutParams.width != -1) &&
/* 229 */             (layoutParams.width != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp363_362 = layoutParams; tmp363_362.width = ((int)(tmp363_362.width * scaleX));
/* 231 */           }if ((layoutParams.height != -2) &&
/* 232 */             (layoutParams.height != -1) &&
/* 233 */             (layoutParams.height != -1))
/*     */           {
/*     */             ViewGroup.LayoutParams tmp402_401 = layoutParams; tmp402_401.height = ((int)(tmp402_401.height * scaleX));
/*     */           }
/*     */
/*     */         }
/*     */
/*     */       }
/*     */       else if (!rootLayout.getTag().toString().equals("DontTouchMe"))
/*     */ {
/* 244 */
                if ((layoutParams.width != -2) &&
/* 245 */           (layoutParams.width != -1) &&
/* 246 */           (layoutParams.width != -1))
/*     */ {
/*     */
                    ViewGroup.LayoutParams tmp444_443 = layoutParams;
                    tmp444_443.width = ((int) (tmp444_443.width * scaleX));
/*     */
                }
/*     */
/* 254 */
                if ((layoutParams.height != -2) &&
/* 255 */           (layoutParams.height != -1) &&
/* 256 */           (layoutParams.height != -1))
/*     */ {
/*     */
                    ViewGroup.LayoutParams tmp483_482 = layoutParams;
                    tmp483_482.height = ((int) (tmp483_482.height * scaleY));
/*     */
                }
            }
/*     */     }
/*     */     else {
/* 262 */       if ((layoutParams.width != -2) &&
/* 263 */         (layoutParams.width != -1) &&
/* 264 */         (layoutParams.width != -1))
/*     */       {
/*     */         ViewGroup.LayoutParams tmp525_524 = layoutParams; tmp525_524.width = ((int)(tmp525_524.width * scaleX));
/*     */       }
/*     */
/* 271 */       if ((layoutParams.height != -2) &&
/* 272 */         (layoutParams.height != -1) &&
/* 273 */         (layoutParams.height != -1))
/*     */       {
/*     */         ViewGroup.LayoutParams tmp564_563 = layoutParams; tmp564_563.height = ((int)(tmp564_563.height * scaleY));
/*     */       }
/*     */
/*     */     }
/*     */
/* 280 */     if ((layoutParams instanceof ViewGroup.MarginLayoutParams))
/*     */     {
/* 282 */       ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)layoutParams;
/*     */       ViewGroup.MarginLayoutParams tmp590_589 = marginParams; tmp590_589.leftMargin = ((int)(tmp590_589.leftMargin * scaleX));
/*     */       ViewGroup.MarginLayoutParams tmp590_5891 = marginParams; tmp590_5891.setMarginStart(((int)(tmp590_5891.getMarginStart() * scaleX)));
/*     */       ViewGroup.MarginLayoutParams tmp604_603 = marginParams; tmp604_603.topMargin = ((int)(tmp604_603.topMargin * scaleY));
/*     */       ViewGroup.MarginLayoutParams tmp618_617 = marginParams; tmp618_617.rightMargin = ((int)(tmp618_617.rightMargin * scaleX));
/*     */       ViewGroup.MarginLayoutParams tmp618_6171 = marginParams; tmp618_6171.setMarginEnd(((int)(tmp618_6171.getMarginEnd() * scaleX)));
/*     */       ViewGroup.MarginLayoutParams tmp632_631 = marginParams; tmp632_631.bottomMargin = ((int)(tmp632_631.bottomMargin * scaleY));
/*     */     }
/*     */
/* 294 */     rootLayout.setLayoutParams(layoutParams);
/*     */
/* 298 */     rootLayout.setPaddingRelative(
/* 300 */       (int)(rootLayout.getPaddingStart() * scaleX),
/* 302 */       (int)(rootLayout.getPaddingTop() * scaleY),
/* 304 */       (int)(rootLayout.getPaddingEnd() * scaleX),
/* 306 */       (int)(rootLayout.getPaddingBottom() * scaleY));

/* 312 */     if ((rootLayout instanceof ViewGroup))
/*     */     {
/* 314 */       ViewGroup vg = (ViewGroup)rootLayout;
/*     */
/* 316 */       for (int i = 0; i < vg.getChildCount(); i++)
/* 317 */         if (vg.getChildAt(i).getTag() != null)
/*     */         {
/* 319 */           if (!vg.getChildAt(i).getTag().toString()
/* 319 */             .equalsIgnoreCase("DontTouchMe"))
/* 320 */             DoMagic(vg.getChildAt(i));
/*     */         }
/*     */         else
/* 323 */           DoMagic(vg.getChildAt(i));
/*     */     }
/*     */   }


/*     */ }

/* Location:           /Users/shankar/Desktop/assl_sdk_rmn_jugnoo.jar
 * Qualified Name:     rmn.androidscreenlibrary.ASSL
 * JD-Core Version:    0.6.2
 */
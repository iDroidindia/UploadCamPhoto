package photo.cam.upload.idi.uploadcamphoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class Cam_Activity extends Activity {

	private static final int CAM_REQUEST_CODE = 2222;  
	
	ImageView IV;
	File F_Destination;
    String Str_Image_Path;
    TextView TV_Path;
    Button BT_Cam, BT_Upload;
    File F_Path;
    Bitmap BMP_File;
    ProgressDialog PG;
    
    String URL = "http://idroidindia.com/apis/upload.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_cam); 	       
		  
		         IV = (ImageView) this.findViewById(R.id.iv);
		         TV_Path = (TextView)findViewById(R.id.tv);
		         
		         SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		         String name = sdf.format(new Date());
		         		       
		         F_Destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
		         
		         BT_Cam = (Button) this.findViewById(R.id.bt_click);
		         BT_Upload = (Button) this.findViewById(R.id.bt_upload);
		  
		         BT_Cam.setOnClickListener(new View.OnClickListener() {  
		  
		         @Override  
		         public void onClick(View v) {  
		              Intent I_CAM = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		              I_CAM.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(F_Destination));
		              startActivityForResult(I_CAM, CAM_REQUEST_CODE);  
		         }  
		        });
		         
		         BT_Upload.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub			
						
						if (Str_Image_Path != null)
						{	
							upload(Str_Image_Path);
							Toast.makeText(getApplicationContext(),"Hello.....Image Uploaded Successfully", Toast.LENGTH_LONG).show();
						}
						else
							Toast.makeText(getApplicationContext(),"Cannot Upload Image, Image Path is empty", Toast.LENGTH_LONG).show();	
					}
				});
		         
		}
		  
		     protected void onActivityResult(int requestCode, int resultCode, Intent I_CAM_B) {  
		      if (requestCode == CAM_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {  
		    	  
		    	  FileInputStream in;
		    	  
		    		try {
					in = new FileInputStream(F_Destination);
				
	                BitmapFactory.Options B_Options = new BitmapFactory.Options();
	                B_Options.inSampleSize = 10;
	                Str_Image_Path = F_Destination.getAbsolutePath();
	                TV_Path.setText(Str_Image_Path);
	                Bitmap BMP_File = BitmapFactory.decodeStream(in, null, B_Options ); 
		    	 
		       IV.setImageBitmap(BMP_File);
		       BT_Upload.setVisibility(View.VISIBLE);
		       
		    		} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
		      }  
		      
		      else 
		      {
		    	  TV_Path.setText("There is some issue while clicking the Photo.");
		      }
		   }
		     
		     public void upload(String File_Path){
		    	 {
						// TODO Auto-generated method stub					 
						//PG.setMessage("Uploading Image");
						//PG.setCancelable(false);
						
						
						//Str_Image_Path = F_Destination.getAbsolutePath();
						
						Toast.makeText(getApplicationContext(),"Start"+File_Path, Toast.LENGTH_LONG).show();
						
						File myFile = new File(Str_Image_Path);
						RequestParams RP = new RequestParams();
						try {
						    RP.put("Image", myFile);
						} catch(FileNotFoundException e) {}
						
					          
					       AsyncHttpClient client = new AsyncHttpClient();
							
							client.post(URL, RP, new JsonHttpResponseHandler(){

								@Override
								public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
									super.onFailure(statusCode, headers, responseString, throwable);

									System.out.println("Failure Response ::: " + responseString);

								}

								@Override
								public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
									super.onSuccess(statusCode, headers, response);

									Log.e("Server Response", response.toString());
								}

							});
					}
				};
	      }  
	      
	
	
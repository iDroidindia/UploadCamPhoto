package photo.cam.upload.idi.uploadcamphoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Upload_Cam_Activity  extends AppCompatActivity {
    private static final int CAM_REQUEST_CODE = 2222;

    ImageView IV;
    File F_Destination;
    String Str_Image_Path;
    TextView TV_Path;
    Button BT_Cam, BT_Upload;
    File F_Path;
    Bitmap BMP_File;
    ProgressDialog progressDialog;

    //String URL = "http://idroidindia.com/apis/upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_act_cam);

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
                    //upload(Str_Image_Path);
                    new UploadFileAsync().execute(Str_Image_Path);
                    Toast.makeText(getApplicationContext(), "Hello.....Image Uploaded Successfully", Toast.LENGTH_LONG).show();
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



           /* client.post(URL, RP, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if(statusCode==200&&response!=null){

                    }
                }
                @Override
                public void sendProgressMessage(int bytesWritten, int bytesTotal) {
                    mCallback.progressUpdate(bytesWritten, bytesTotal);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });*/
        }
    }
    public class UploadFileAsync extends AsyncTask<String, Integer, Void> {
        String resServer;

        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Upload_Cam_Activity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            //progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            //position = Integer.parseInt(params[0]);

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 5 * 1024 * 1024;
            int resCode = 0;
            String resMessage = "";

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            // File Path
            //String strSDPath = ImageList.get(position).toString();

            // Upload to PHP Script
            String strUrlServer = "http://idroidindia.com/apis/uploadam.php";

            try {
                /** Check file on SD Card ***/
                File file = new File(Str_Image_Path);
                if(!file.exists())
                {
                    resServer = "{\"StatusID\":\"0\",\"Error\":\"Please check path on SD Card\"}";
                    return null;
                }

                FileInputStream fileInputStream = new FileInputStream(new File(Str_Image_Path));

                java.net.URL url = new URL(strUrlServer);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"Image\";filename=\"" + Str_Image_Path + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                int  progress=0;
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    progress+=bytesRead;
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    publishProgress(progress);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Response Code and  Message
                resCode = conn.getResponseCode();
                if(resCode == HttpURLConnection.HTTP_OK)
                {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int read = 0;
                    while ((read = is.read()) != -1) {
                        bos.write(read);
                    }
                    byte[] result = bos.toByteArray();
                    bos.close();

                    resMessage = new String(result);

                }

                Log.d("resCode=", Integer.toString(resCode));
                Log.d("resMessage=",resMessage.toString());

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                resServer = resMessage.toString();


            } catch (Exception ex) {
                // Exception handling
                return null;
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress((int)progress[0]);
        }
        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            //statusWhenFinish(resServer);
            /*** Default Value ***/
            String strStatusID = "0";
            String strError = "Unknown Status!";

            // Status
            TextView status = (TextView)findViewById(R.id.uploadStatus);


            try {
                JSONObject c = new JSONObject(resServer);
                strStatusID = c.getString("StatusID");
                strError = c.getString("Error");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Prepare Status
            if(strStatusID.equals("0"))
            {
                // When update Failed
                status.setText("Upload Failed. ("+ strError +")");
                status.setTextColor(Color.RED);

                // Enabled Button again
                Button btnUpload = (Button) findViewById(R.id.bt_upload);
                btnUpload.setText("Re-try");
                btnUpload.setTextColor(Color.RED);
                btnUpload.setEnabled(true);
            }
            else
            {
                status.setText("Upload Completed.");
                status.setTextColor(Color.RED);
            }
        }
    }


}


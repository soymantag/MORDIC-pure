package com.chris.mordic_pure.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.mordic_pure.R;
import com.chris.mordic_pure.data.WordBean;
import com.chris.mordic_pure.data.WordbookBean;
import com.chris.mordic_pure.db.WordDao;
import com.chris.mordic_pure.db.WordbookDao;
import com.chris.mordic_pure.db.WordbookListDao;
import com.chris.mordic_pure.protocol.WordProtocol;
import com.chris.mordic_pure.utils.SwipeView;
import com.chris.mordic_pure.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ListView mListview;
    private List<WordbookBean> mDatas = new ArrayList<>();
    private RelativeLayout mLayout_show_words_num;
    private TextView mTv_improtWord;
    private MyAdapter mWordbookListAdapter;
    private int mAllWordsNum = 0;
    private int mUnlearnedWordsNum = 0;
    private int mLearnedWordsNum = 0;
    private int mReciteWordsNum = 0;
    private TextView mTv_allWordsNum;
    private TextView mTv_learnedNum;
    private TextView mTv_reciteNum;
    private TextView mTv_unlearnedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        }
        setContentView(R.layout.fragment_wordbook);
        mListview = (ListView) findViewById(R.id.listView_wordbook);
        mTv_improtWord = (TextView) findViewById(R.id.tv_importWord);

        mLayout_show_words_num = (RelativeLayout) View.inflate(this, R.layout.layout_show_words_num, null);
        mTv_allWordsNum = (TextView) mLayout_show_words_num.findViewById(R.id.tv_allWordsNum);
        mTv_learnedNum = (TextView) mLayout_show_words_num.findViewById(R.id.tv_learnedNum);
        mTv_reciteNum = (TextView) mLayout_show_words_num.findViewById(R.id.tv_reciteNum);
        mTv_unlearnedNum = (TextView) mLayout_show_words_num.findViewById(R.id.tv_unlearnedNum);

        initNum();

        mListview.addHeaderView(mLayout_show_words_num);//addHeaderView要在setAdapter前调用,否则会出错
        mWordbookListAdapter = new MyAdapter();
        mListview.setAdapter(mWordbookListAdapter);
        mTv_improtWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setDataAndType(Uri.fromFile(new File("/sdcard")),"*/*");
                intent.setDataAndType(Uri.fromFile(Environment.getExternalStorageDirectory().getParentFile()), "*/*");
                intent.setClass(MainActivity.this, FileSelectActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initNum() {
        mTv_allWordsNum.setText(mAllWordsNum + "");
        mTv_learnedNum.setText(mLearnedWordsNum + "");
        mTv_unlearnedNum.setText(mUnlearnedWordsNum + "");
        mTv_reciteNum.setText(mReciteWordsNum + "");
    }

    private void initData() {
        mDatas.clear();
        mAllWordsNum = 0;
        mUnlearnedWordsNum = 0;
        mLearnedWordsNum = 0;
        mReciteWordsNum = 0;
        initWordbookListDatas();
        WordbookDao wordbookDao = new WordbookDao(MainActivity.this);
        for (WordbookBean wordbookBean : mDatas) {
            mAllWordsNum += wordbookBean.getSum();
            mUnlearnedWordsNum += wordbookDao.getUnlearnedWordsNum(wordbookBean.getBookName());
            mLearnedWordsNum += wordbookDao.getLearnedWordsNum(wordbookBean.getBookName());
            mReciteWordsNum += wordbookDao.getReciteWordsNum(wordbookBean.getBookName());
        }
    }

    private void initWordbookListDatas() {
        WordbookListDao wordbookListDao = new WordbookListDao(MainActivity.this);
        mDatas = wordbookListDao.getAllWordbook();
    }

    private class ViewHolder {
        ImageView im_icon;
        SwipeView sv;
        TextView tv_bookName;
        TextView tv_wordsNum;
        TextView item_tv_delete;
        TextView item_tv_top;
        RelativeLayout mRl_item_wordbooklist;
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_wordbooklist, null);
                viewHolder = new ViewHolder();
                viewHolder.sv = (SwipeView) convertView.findViewById(R.id.item_sv);
                viewHolder.mRl_item_wordbooklist = (RelativeLayout) convertView.findViewById(R.id.rl_item_wordbooklist);
                viewHolder.im_icon = (ImageView) convertView.findViewById(R.id.iv_wordbook);
                viewHolder.tv_bookName = (TextView) convertView.findViewById(R.id.tv_wordbookName);
                viewHolder.tv_wordsNum = (TextView) convertView.findViewById(R.id.tv_wordsNum);
                viewHolder.item_tv_delete = (TextView) convertView.findViewById(R.id.item_tv_delete);
                viewHolder.item_tv_top = (TextView) convertView.findViewById(R.id.item_tv_top);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.sv.reset();

            final WordbookBean bean = mDatas.get(position);
            viewHolder.tv_bookName.setText(bean.getBookName());
            viewHolder.tv_wordsNum.setText(String.valueOf(bean.getSum()));
            viewHolder.sv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this,position+"OnClick",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, WordActivity.class);
                    intent.putExtra("wordbook", bean.getBookName());
                    startActivity(intent);
                }
            });
            viewHolder.item_tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("删除生词本")
                            .setMessage("确定要删除\"" + mDatas.get(position).getBookName() + "\"吗?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new WordbookListDao(MainActivity.this).deleteWordbook(mDatas.get(position).getBookName());
                                    initData();
                                    initNum();
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            });
            viewHolder.item_tv_top.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new WordbookListDao(MainActivity.this).add(bean.getBookName(), bean.getIndex_disordered(), bean.getIndex_ordered(), bean.getSum());
                    initWordbookListDatas();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();
        initNum();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                final String path = data.getStringExtra("path");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("导入生词")
                        .setMessage("已选择的生词本路径为" + path + ",是否导入?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "导入中...", Toast.LENGTH_SHORT).show();
                                ImportWordsFromDisk(path);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        }
    }

    private void ImportWordsFromDisk(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog alertDialog = builder.setTitle("导入生词")
                        .setMessage("请勿退出,且保持网络连接,正在导入生词...")
                        .setCancelable(false)
                        .create();
                alertDialog.show();
                new Thread(new Runnable() {

                    private Reader mReader;
                    private BufferedReader mBufferedReader;

                    @Override
                    public void run() {
                        try {

                            File file = new File(path);
                            String fileName = file.getName();
                            mReader = new FileReader(path);
                            mBufferedReader = new BufferedReader(mReader);
                            String line;

                            WordProtocol wordProtocol = new WordProtocol();
                            WordDao wordDao = new WordDao(MainActivity.this);
                            WordbookDao wordbookDao = new WordbookDao(MainActivity.this);

                            WordbookListDao wordbookListDao = new WordbookListDao(MainActivity.this);
                            if (wordbookListDao.getData("c_" + fileName) == null) {
                                wordbookDao.createTable("c_" + fileName);
                            }
                            while ((line = mBufferedReader.readLine()) != null) {

                                System.out.println("$$$$$$line:" + line);
                                if (line.contains("&&")) {

                                } else {
                                    String word = line.trim();
                                    WordBean wordBean = wordProtocol.loadData(word);

                                    System.out.println("wordBean.word_name:" + wordBean.word_name);
                                    if (wordBean.word_name == null) {
                                        continue;
                                    }
                                    //序列化一个对象(存储到字节数组)
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                                    oos.writeObject(wordBean);

                                    wordDao.replace(word, baos.toByteArray(), "0");
                                    System.out.println("replace into is ok");
                                    wordbookDao.add("c_" + fileName, word, "unlearned");

                                    WordDao wordDao1 = new WordDao(MainActivity.this);
                                    byte[] beanByte = wordDao1.getBean(word);
                                    //反序列化,将该对象恢复(存储到字节数组)
                                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(beanByte));
                                    WordBean p = (WordBean) ois.readObject();
                                    System.out.println("wordname:" + p.word_name);
                                }
                            }

                            wordbookListDao.add("c_" + fileName, 1, 1, wordbookDao.getTotalRows("c_" + fileName));
                            mDatas = wordbookListDao.getAllWordbook();
                            initData();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initNum();
                                    mWordbookListAdapter.notifyDataSetChanged();
                                }
                            });


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            //Toast.makeText(UIUtils.getContext(), "错误:文件未找到", Toast.LENGTH_SHORT).show();
                            System.out.println("错误:文件未找到");
                        } catch (IOException e) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UIUtils.getContext(), "导入失败,文件不可读", Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        } catch (Exception e) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UIUtils.getContext(), "导入失败,请检查网络连接", Toast.LENGTH_SHORT).show();
                                }
                            });

                            System.out.println("导入失败,请检查网络连接");
                            e.printStackTrace();
                        } finally {
                            try {
                                mReader.close();
                                mBufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        System.out.println("导入成功.");
                        alertDialog.dismiss();
                    }
                }).start();
            }
        });
    }
}

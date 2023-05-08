package com.example.homework_33;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements Runnable{

    // создание полей
    private MediaPlayer mediaPlayer = new MediaPlayer(); // создание поля медиа-плееlp0ра
    private SeekBar seekBar; // создание поля SeekBar
    private boolean wasPlaying = false; // поле проигрывания аудио-файла
    private FloatingActionButton fabPlayPause; // поле кнопки проигрывания и постановки на паузу аудиофайла
    private FloatingActionButton fabBack; // поле кнопки перемотки назад аудиофайла
    private FloatingActionButton fabForward; // поле кнопки перемотки вперед аудиофайла
    private FloatingActionButton fabRepeat; // поле кнопки повтора
    private FloatingActionButton fabStop; // поле кнопки стоп
    private TextView seekBarHint; // поле информации у SeekBar
    private TextView metaDataAudio; // создание поля вывода информации о треке
    private String metaData; // поле для записи метаданных
    private boolean isRepeat = false; // поле выключенного повтора трека

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // присваивание полям id ресурсов
        fabPlayPause = findViewById(R.id.fabPlayPause);
        fabBack = findViewById(R.id.fabBack);
        fabRepeat = findViewById(R.id.fabRepeat);
        fabForward = findViewById(R.id.fabForward);
        fabStop = findViewById(R.id.fabStop);
        seekBarHint = findViewById(R.id.seekBarHint);
        seekBar = findViewById(R.id.seekBar);
        metaDataAudio = findViewById(R.id.metaDataAudio);

        // создание слушателя изменения SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // метод при перетаскивании ползунка по шкале,
            // где progress позволяет получить нове значение ползунка (позже progress назрачается длина трека в миллисекундах)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE); // установление видимости seekBarHint
                //seekBarHint.setVisibility(View.INVISIBLE); // установление не видимости seekBarHint

                // Math.ceil() - округление до целого в большую сторону
                int timeTrack = (int) Math.ceil(progress/1000f); // перевод времени из миллисекунд в секунды

                // вывод на экран времени отсчёта трека
                if (timeTrack < 10) {
                    seekBarHint.setText("00:0" + timeTrack);
                } else if (timeTrack < 60){
                    seekBarHint.setText("00:" + timeTrack);
                } else if (timeTrack >= 60 && timeTrack < 70) {
                    seekBarHint.setText("01:0" + (timeTrack - 60));
                } else if (timeTrack >= 70) {
                    seekBarHint.setText("01:" + (timeTrack - 60));
                }

                // передвижение времени отсчёта трека
                double percentTrack = progress / (double) seekBar.getMax(); // получение процента проигранного трека (проигранное время делится на длину трека)
                // seekBar.getX() - начало seekBar по оси Х
                // seekBar.getWidth() - ширина контейнера seekBar
                //0.92 - поправочный коэффициент (так как seekBar занимает не всю ширину своего контейнера)
                seekBarHint.setX(seekBar.getX() + Math.round(seekBar.getWidth()*percentTrack*0.92));

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) { // если mediaPlayer не пустой и mediaPlayer не воспроизводится

                    // назначение кнопке картинки play
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                }
            }
            // метод при начале перетаскивания ползунка по шкале
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.INVISIBLE); // установление видимости seekBarHint
            }
            // метод при завершении перетаскивания ползунка по шкале
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) { // если mediaPlayer не пустой и mediaPlayer воспроизводится
                    mediaPlayer.seekTo(seekBar.getProgress()); // обновление позиции трека при изменении seekBar
                }
            }
        });

        // обработка нажатия кнопки
        fabPlayPause.setOnClickListener(listener); // создание слушателя нажатия кнопки fabPlayPause
        fabBack.setOnClickListener(listener); // создание слушателя нажатия кнопки fabBack (перемотки назад)
        fabForward.setOnClickListener(listener); //создание слушателя нажатия кнопки fabForward (перемотки вперед)
        fabStop.setOnClickListener(listener); // создание слушателя кнопки fabStop (стоп)
        fabRepeat.setOnClickListener(listener); // создание слушателя нажатия кнопки fabRepeat (повтора)


    }

    // создание слушателя обработки нажания кнопки
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            // обработка нажатия нескольких кнопок
            switch (view.getId()) {
                case R.id.fabPlayPause:
                    playSong(); //запуск playSong
                    break;
                case R.id.fabBack:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000); // перематывание трека назад на 5 секунд (5000 миллисекунд)
                    break;
                case R.id.fabForward:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000); // перематывание трека вперед на 5 секунд (5000 миллисекунд)
                    break;
                case R.id.fabStop:
                    if (mediaPlayer.isPlaying()) { // если mediaPlayer играет
                        mediaPlayer.stop(); // mediaPlayer остоновить
                        mediaPlayer = null; // mediaPlayer пустой
                        seekBar.setProgress(0); // seekBar = 0
                        // назначение кнопке картинки play
                        fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                    }
                    break;
                case R.id.fabRepeat:
                    if (!isRepeat && mediaPlayer != null) {
                        mediaPlayer.setLooping(true); // включение повтора аудио
                        // назначение кнопке картинки repeat
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.repeat));
                        isRepeat = true; // изменение значения переключения
                    } else if (isRepeat && mediaPlayer != null){
                        mediaPlayer.setLooping(false); // выключение повтора аудио
                        // назначение кнопке картинки repeat_off
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.repeat_off));
                        isRepeat = false; // изменение значения переключения
                    }
                    break;
            }
        }
    };


    // метод запуска аудио-файла
    public void playSong() {

        try { // обработка исключения на случай отстутствия файла
            if (mediaPlayer == null) { // если mediaPlayer пустой
                mediaPlayer = new MediaPlayer(); // то выделяется для него память
            }

            if (mediaPlayer.isPlaying()) { // если mediaPlayer воспроизводится
                mediaPlayer.pause(); // mediaPlayer пауза
                wasPlaying = true; // инициализация значения запуска аудио-файла
                // назначение кнопке картинки play
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            }

            if (!wasPlaying) {
                mediaPlayer.start();
                // назначение кнопке картинки pause
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));

                // альтернативный способ считывания файла с помощью файлового дескриптора
                AssetFileDescriptor descriptor = getAssets().openFd("trek_1.mp3");
                // запись файла в mediaPlayer, задаются параметры (путь файла, смещение относительно начала файла, длина аудио в файле)
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());

                // запись метаданных в окно вывода информации metaDataAudio
                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever(); // осздание объекта специального класса для считывания метаданных
                mediaMetadata.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength()); // считывание метаданных по имеющемуся дескриптору

                metaData = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // добавление названия трека
                metaData += "\n" + mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); // добавление артиста

                mediaMetadata.release(); // закрытие объекта загрузки метаданных

                metaDataAudio.setText(metaData); // вывод метаданных в окно metaDataAudio

                descriptor.close(); // закрытие дескриптора

                mediaPlayer.prepare(); // ассинхронная подготовка плейера к проигрыванию
                mediaPlayer.setLooping(false); // назначение отстутствия повторов
                seekBar.setMax(mediaPlayer.getDuration()); // ограниечение seekBar длинной трека

                mediaPlayer.start(); // старт mediaPlayer
                new Thread(this).start(); // запуск дополнительного потока
            }

            wasPlaying = false; // возврат отсутствия проигрывания mediaPlayer

        } catch (Exception e) { // обработка исключения на случай отстутствия файла
            e.printStackTrace(); // вывод в консоль сообщения отсутствия файла
        }
    }



    // метод дополнительного потока для обновления SeekBar
    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition(); // считывание текущей позиции трека
        int total = mediaPlayer.getDuration(); // считывание длины трека

        // бесконечный цикл при условии не нулевого mediaPlayer, проигрывания трека и текущей позиции трека меньше длины трека
        while (mediaPlayer != null) {
            try {

                Thread.sleep(500); // засыпание вспомогательного потока на 1 секунду
                currentPosition = mediaPlayer.getCurrentPosition(); // обновление текущей позиции трека

            } catch (InterruptedException e) { // вызывается в случае блокировки данного потока
                e.printStackTrace();
                return; // выброс из цикла
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition); // обновление seekBar текущей позицией трека

        }
    }
}